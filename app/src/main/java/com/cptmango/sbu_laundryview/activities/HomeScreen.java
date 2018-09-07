package com.cptmango.sbu_laundryview.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.adapters.HomeScreenFragmentPagerAdapter;
import com.cptmango.sbu_laundryview.adapters.MachineGridStatusAdapter;
import com.cptmango.sbu_laundryview.background.NotifyUser;
import com.cptmango.sbu_laundryview.data.DataManager;
import com.cptmango.sbu_laundryview.data.model.Machine;
import com.cptmango.sbu_laundryview.data.model.Room;
import com.cptmango.sbu_laundryview.ui.Animations;
import com.cptmango.sbu_laundryview.ui.Utilities;

public class HomeScreen extends AppCompatActivity {

    DataManager dataManager;
    GridView washerGrid;
    GridView dryerGrid;
    GridView favoriteGrid;
    ViewPager pager;
    View refreshed;
    MachineGridStatusAdapter washerAdapter;
    MachineGridStatusAdapter dryerAdapter;
    MachineGridStatusAdapter favoriteAdapter;
    HomeScreenFragmentPagerAdapter pagerAdapter;

    BottomNavigationView bottomNavigationView;

    boolean appPaused = false;

    String quadName;
    String buildingName;
    String quadColor;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Log.i("LOG", "App launched successfully. Checking for user preferences.");

        context = this;
        initialCheck();
        createNotificationChannel();
    }

    /**
     * Performed every time the app is run.
     * Checks whether the user has chosen a room yet.
     * If not, then launches Room Selection Activity.
     */
    void initialCheck(){

        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

//        ENABLE THIS ONCE TESTING DONE
        if(!prefs.contains("quad")){

            Log.i("LOG", "User preferences not found. Launching room select activity.");

            // Setup User Defaults
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putInt("reminder", 2);
            editor.apply();

            // Start activity to select a room.
            Intent intent = new Intent(this, SelectRoom.class);
            startActivityForResult(intent, 1);

        }
        // The user has already previously selected a room.
        else {
            Log.i("LOG", "User preferences found. Connecting to API.");
            connectToAPI();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {

        // Selected Room
        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                connectToAPI();
                appPaused = false;
            }
        }
        // Changed Room
        else if (requestCode == 2){
            if(resultCode == RESULT_OK){
                this.recreate();
                dataManager.clearUserFavorites(this);
                favoriteAdapter.notifyDataSetChanged();
            }else {

            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(appPaused){
            Log.i("LOG", "App has returned from background.");
            updateData();
            appPaused = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("LOG", "App has entered the background.");
        appPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataManager.saveFavoritesToPreferences();
    }

    @Override
    public void onBackPressed() {

        View machineMenu = findViewById(R.id.machine_menu);
        View machineBG = findViewById(R.id.bg);

        // If the machine info menu is showing, hide it.
        if(machineMenu.getAlpha() == 1.0){
            Animations.hide(machineMenu);
            Animations.hide(machineBG);
        }
        // Do regular back button stuff (exit the app/activity).
        else {
            super.onBackPressed();
        }

    }

    void connectToAPI() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        quadName = prefs.getString("quad", "Mendelsohn");
        buildingName = prefs.getString("building", "Irving");
        quadColor = prefs.getString("quadColor", "#f1c40f");

        dataManager = new DataManager(this, quadName, buildingName);
        dataManager.getData();

        dataManager.getQueue().addRequestFinishedListener(
            request -> {
                if (pager == null && dataManager.getRoomData() != null) {
                    dataManager.loadFavoritesFromPreferences(context);

                    Log.i("LOG", "Initializing UI.");
                    initializeUI();

                } else {
                    return;
                }
            }
        );

    }

    void initializeUI(){

        //Setting up view pager.
        pagerAdapter = new HomeScreenFragmentPagerAdapter(getSupportFragmentManager(), this);
        pager = (ViewPager) findViewById(R.id.pager_HomeScreen);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1);
        pager.setOffscreenPageLimit(3);

        showFavoriteData();
        showWasherData();
        showDryerData();
        showSummaryPage();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_summary);

        // SETTING UP LISTENERS

        //Bottom Tab Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            item.setChecked(true);

            switch(item.getItemId()){

                case R.id.nav_washers:
                    pager.setCurrentItem(0, true);
//                        showWasherData();
                    break;

                case R.id.nav_summary:
                    pager.setCurrentItem(1, true);
                    break;

                case R.id.nav_dryers:
                    pager.setCurrentItem(2, true);
//                        showDryerData();
                    break;

            }

            return false;
        });
        // Pager Listener
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                // Selected Washers
                if(position == 0){
                    //showWasherData();
                    bottomNavigationView.setSelectedItemId(R.id.nav_washers);
                }
                // Selected Dryers
                else if(position == 2){
                    //showDryerData();
                    bottomNavigationView.setSelectedItemId(R.id.nav_dryers);

                }
                else if(position == 1){
                    ScrollView view = findViewById(R.id.summary_scrollView);
                    view.fullScroll(View.FOCUS_UP);
                    view.scrollTo(0,0);

                    bottomNavigationView.setSelectedItemId(R.id.nav_summary);

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        // Swipe Refresh Listener
        SwipeRefreshLayout.OnRefreshListener listener = this::updateData;
        SwipeRefreshLayout dryerRefresh = findViewById(R.id.tab_dryers);
        SwipeRefreshLayout washerRefresh = findViewById(R.id.tab_washers);
        SwipeRefreshLayout summaryRefresh = findViewById(R.id.tab_summary);
        dryerRefresh.setOnRefreshListener(listener);
        washerRefresh.setOnRefreshListener(listener);
        summaryRefresh.setOnRefreshListener(listener);

        // Data Refresh Listener
        dataManager.getQueue().addRequestFinishedListener(response -> {

            // Stop Refreshing
            dryerRefresh.setRefreshing(false);
            washerRefresh.setRefreshing(false);
            summaryRefresh.setRefreshing(false);

            if(dataManager.getRoomData() != null){
                refreshed.setVisibility(View.VISIBLE);
                refreshed
                        .animate()
                        .translationY(0)
                        .setDuration(200)
                        .setInterpolator(new LinearInterpolator())
                        .withEndAction(() -> {
                            refreshed.animate().translationY(-100).setDuration(300).setInterpolator(new LinearInterpolator()).setStartDelay(1000)
                                    .withEndAction(() -> {
                                        refreshed.setVisibility(View.GONE);
                                    });

                        });

                updateSummaryPage();
                washerAdapter.notifyDataSetChanged();
                dryerAdapter.notifyDataSetChanged();
                favoriteAdapter.notifyDataSetChanged();

            }

        });

        TextView quadNameText = (TextView) findViewById(R.id.txt_quadName);
        View machineMenu = findViewById(R.id.machine_menu); machineMenu.setTranslationY(50);

        refreshed = findViewById(R.id.network_status); refreshed.setTranslationY(-100); refreshed.setVisibility(View.GONE);

        // Setting up title bar.
        Utilities.changeStatusBarColor(getWindow(), quadColor);

        TextView buildingNameText = (TextView) findViewById(R.id.txt_buildingName);
        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.btn_refresh);
        quadNameText.setText(quadName.toUpperCase());
        buildingNameText.setText(buildingName);

        ImageView colorL = (ImageView) findViewById(R.id.img_highlightL);
        ImageView colorR = (ImageView) findViewById(R.id.img_highlightR);
        ImageView lineL = (ImageView) findViewById(R.id.line_left);
        ImageView lineR = (ImageView) findViewById(R.id.line_right);

        // Changing the color of UI elements to match the quad's color.
        refresh.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(quadColor)));
        refresh.setOnClickListener(v -> updateData());
        refresh.setVisibility(View.GONE);

        colorL.setColorFilter(Color.parseColor(quadColor));
        colorR.setColorFilter(Color.parseColor(quadColor));
        lineL.setColorFilter(Color.parseColor(quadColor));
        lineR.setColorFilter(Color.parseColor(quadColor));
    }

    void updateData(){

        Log.i("LOG", "Refreshing data.");

        dataManager.getData();
        // Show Refreshing
        SwipeRefreshLayout dryerRefresh = findViewById(R.id.tab_dryers);
        SwipeRefreshLayout washerRefresh = findViewById(R.id.tab_washers);
        SwipeRefreshLayout summaryRefresh = findViewById(R.id.tab_summary);
        dryerRefresh.setRefreshing(true);
        washerRefresh.setRefreshing(true);
        summaryRefresh.setRefreshing(true);

    }

    void showSummaryPage(){

        TextView summary_washerAvailable = (TextView) findViewById(R.id.txt_washerAvailable);
        TextView summary_dryerAvailable = (TextView) findViewById(R.id.txt_dryerAvailable);
        ImageView washerIcon = (ImageView) findViewById(R.id.img_washerStatus);
        ImageView dryerIcon = (ImageView) findViewById(R.id.img_dryerStatus);

        if(dataManager.getRoomData().totalFavorites() == 0)
            findViewById(R.id.img_notFound).setVisibility(View.VISIBLE);
        else{
            favoriteGrid.setVisibility(View.VISIBLE);
            findViewById(R.id.img_notFound).setVisibility(View.GONE);
        }

        Room roomData = dataManager.getRoomData();
        String[] numbers = context.getResources().getStringArray(R.array.machine_status_numbers);
        boolean dryers_available = roomData.dryers_available() != 0;
        boolean washers_available = roomData.washers_available() != 0;

        if(dryers_available){
            summary_dryerAvailable.setText(numbers[roomData.dryers_available()] + " available.");
            dryerIcon.setColorFilter(ContextCompat.getColor(context, R.color.Green));
        }else {
            summary_dryerAvailable.setText(numbers[roomData.dryers_available()] + "");
            dryerIcon.setColorFilter(ContextCompat.getColor(context, R.color.Red));
        }

        if(washers_available){
            summary_washerAvailable.setText(numbers[roomData.washers_available()] +  " available.");
            washerIcon.setColorFilter(ContextCompat.getColor(context, R.color.Green));
        }else {
            summary_washerAvailable.setText(numbers[roomData.washers_available()] + "");
            washerIcon.setColorFilter(ContextCompat.getColor(context, R.color.Red));
        }

    }

    void updateSummaryPage(){
        TextView summary_washerAvailable = (TextView) findViewById(R.id.txt_washerAvailable);
        TextView summary_dryerAvailable = (TextView) findViewById(R.id.txt_dryerAvailable);
        ImageView washerIcon = (ImageView) findViewById(R.id.img_washerStatus);
        ImageView dryerIcon = (ImageView) findViewById(R.id.img_dryerStatus);

        Room roomData = dataManager.getRoomData();
        String[] numbers = context.getResources().getStringArray(R.array.machine_status_numbers);
        boolean dryers_available = roomData.dryers_available() != 0;
        boolean washers_available = roomData.washers_available() != 0;

        if(dryers_available){
            summary_dryerAvailable.setText(numbers[roomData.dryers_available()] + " available.");
            dryerIcon.setColorFilter(ContextCompat.getColor(context, R.color.Green));
        }else {
            summary_dryerAvailable.setText(numbers[roomData.dryers_available()] + "");
            dryerIcon.setColorFilter(ContextCompat.getColor(context, R.color.Red));
        }

        if(washers_available){
            summary_washerAvailable.setText(numbers[roomData.washers_available()] +  " available.");
            washerIcon.setColorFilter(ContextCompat.getColor(context, R.color.Green));
        }else {
            summary_washerAvailable.setText(numbers[roomData.washers_available()] + "");
            washerIcon.setColorFilter(ContextCompat.getColor(context, R.color.Red));
        }
    }

    void showDryerData(){

        dryerGrid = (GridView) pager.findViewById(R.id.grid_dryers);

        // Setting up the dryerGrid view.

        dryerAdapter = new MachineGridStatusAdapter(
                context,
                MachineGridStatusAdapter.AdapterType.DRYER_ADAPTER,
                dataManager.getRoomData()
        );

        dryerGrid.setAdapter(dryerAdapter);
        dryerGrid.setColumnWidth(GridView.AUTO_FIT);
        dryerGrid.setNumColumns(GridView.AUTO_FIT);

    }

    void showFavoriteData(){
        favoriteGrid = pager.findViewById(R.id.grid_favoriteMachines);
        int numberOfFavorites = dataManager.getRoomData().totalFavorites();

        favoriteAdapter = new MachineGridStatusAdapter(
                this,
                MachineGridStatusAdapter.AdapterType.FAVORITES_ADAPTER,
                dataManager.getRoomData()
        );
        favoriteGrid.setAdapter(favoriteAdapter);

        // Resizing grid.
        int numberOfMachines = numberOfFavorites / 2;
        numberOfMachines += ((numberOfFavorites % 2 == 0) ? 0 : 1);
        Utilities.resizeGridViewHeight(favoriteGrid, 200 * (numberOfMachines), context);

        favoriteGrid.setColumnWidth(GridView.AUTO_FIT);
        favoriteGrid.setNumColumns(GridView.AUTO_FIT);
    }

    void showWasherData(){

        washerGrid = (GridView) pager.findViewById(R.id.grid_washers);

//        washerGrid.setEnabled(false);

        // Setting up washer washerGrid view.
        washerAdapter = new MachineGridStatusAdapter(
                context,
                MachineGridStatusAdapter.AdapterType.WASHER_ADAPTER,
                dataManager.getRoomData()
        );

        washerGrid.setColumnWidth(GridView.AUTO_FIT);
        washerGrid.setNumColumns(GridView.AUTO_FIT);
        washerGrid.setAdapter(washerAdapter);
    }

    public void onClick(View view){

        switch(view.getId()){

            case R.id.bg:
                View machineMenu = findViewById(R.id.machine_menu);
                View bg = findViewById(R.id.bg);
                Animations.hideDown(machineMenu);
                Animations.hide(bg);
            break;

            case R.id.btn_favorite:

                TextView number = findViewById(R.id.txt_machineNumber);
                int machineNumber = Integer.parseInt(number.getText().toString());
                dataManager.changeFavoriteStatus(machineNumber);
                int numberOfFavorites = dataManager.getRoomData().totalFavorites();

                // Showing not found icon.
                if(dataManager.getRoomData().totalFavorites() == 0){
                    favoriteGrid.setVisibility(View.GONE);
                    findViewById(R.id.img_notFound).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.img_notFound).setVisibility(View.GONE);
                    favoriteGrid.setVisibility(View.VISIBLE);
                }

                // Resizing grid.
                int numberOfMachines = numberOfFavorites / 2;
                numberOfMachines += ((numberOfFavorites % 2 == 0) ? 0 : 1);
                Utilities.resizeGridViewHeight(favoriteGrid, 200 * (numberOfMachines), context);

                favoriteAdapter.notifyDataSetChanged();
            break;

            case R.id.btn_notify: createNotification();
            break;

            case R.id.btn_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivityForResult(intent, 2);
            break;


            default: return;
        }

    }

    void createNotification(){

        TextView number = (TextView) findViewById(R.id.txt_machineNumber);
        int machineNumber = Integer.parseInt(number.getText().toString());
        Machine machine = dataManager.getRoomData().getMachine(machineNumber - 1);

        if(!dataManager.getRoomData().getMachine(machineNumber - 1).isFavorite()){
            dataManager.changeFavoriteStatus(machineNumber - 1);
            favoriteAdapter.notifyDataSetChanged();
        }

        // Time is set to the minutes left until the machine is done.
        long notificationTime = machine.timeLeft() * 60000;
        // User defined minutes are subtracted from the notification time.
        int userReminderTime = 60_000 * PreferenceManager.getDefaultSharedPreferences(this).getInt("reminder", 2);
        notificationTime -= userReminderTime;
        if(notificationTime < 0){
            Toast.makeText(context, "The machine will be ready soon. No reminder set.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent notificationIntent = new Intent(this, NotifyUser.class);
        notificationIntent.putExtra("machineNumber", machineNumber);
        notificationIntent.putExtra("roomName", buildingName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, machineNumber, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long timeInFuture = SystemClock.elapsedRealtime() + notificationTime;

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeInFuture, pendingIntent);

        Toast.makeText(this, "You'll be notified when the cycle is complete.", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LaundryView";
            String description = "Get reminded when your laundry is done.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("main", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
