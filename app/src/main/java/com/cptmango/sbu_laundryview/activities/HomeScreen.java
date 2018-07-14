package com.cptmango.sbu_laundryview.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.adapters.FavoriteGridStatusAdapter;
import com.cptmango.sbu_laundryview.adapters.HomeScreenFragmentPagerAdapter;
import com.cptmango.sbu_laundryview.adapters.MachineGridStatusAdapter;
import com.cptmango.sbu_laundryview.data.DataManager;
import com.cptmango.sbu_laundryview.data.model.Machine;
import com.cptmango.sbu_laundryview.data.model.MachineStatus;
import com.cptmango.sbu_laundryview.data.model.Room;
import com.cptmango.sbu_laundryview.ui.Animations;
import com.cptmango.sbu_laundryview.ui.GeneralUI;

public class HomeScreen extends AppCompatActivity {

    DataManager data;
    GridView washerGrid;
    GridView dryerGrid;
    GridView favoriteGrid;
    ViewPager pager;
    View refreshed;
    MachineGridStatusAdapter washerAdapter;
    MachineGridStatusAdapter dryerAdapter;
    FavoriteGridStatusAdapter favoriteAdapter;
    HomeScreenFragmentPagerAdapter pagerAdapter;

    SwipeRefreshLayout.OnRefreshListener listener;
    BottomNavigationView bottomNavigationView;

    boolean paused = false;

    String quadName;
    String buildingName;
    String quadColor;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        context = this;
        initialCheck();
    }

    /**
     * Performed every time the app is run.
     * Checks whether the user has chosen a room yet.
     * If not, then launches Room Selection Activity.
     */
    void initialCheck(){

        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Intent intent = new Intent(this, SelectRoom.class);
        startActivityForResult(intent, 1);

        /* ENABLE THIS ONCE TESTING DONE
        if(!prefs.contains("quad")){

            // Start activity to select a room.
            Intent intent = new Intent(this, SelectRoom.class);
            startActivity(intent);

        }
        // The user has already previously selected a room.
        else return;

        */

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                connectToAPI();
                paused = false;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused){
            updateData();
            paused = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        data.saveFavoritesToPreferences();
    }

    @Override
    public void onBackPressed() {

        View machineMenu = findViewById(R.id.machine_menu);

        // If the machine info menu is showing, hide it.
        if(machineMenu.getAlpha() == 1.0){
            Animations.hide(machineMenu);
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
        quadColor = prefs.getString("quadColor", "000000");

        data = new DataManager(this, quadName, buildingName);
        data.getData();

        data.getQueue().addRequestFinishedListener(
            request -> {
                if (pager == null) {
                    data.loadFavoritesFromPreferences();
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
        listener = this::updateData;
        SwipeRefreshLayout dryerRefresh = findViewById(R.id.tab_dryers);
        SwipeRefreshLayout washerRefresh = findViewById(R.id.tab_washers);
        SwipeRefreshLayout summaryRefresh = findViewById(R.id.tab_summary);
        dryerRefresh.setOnRefreshListener(listener);
        washerRefresh.setOnRefreshListener(listener);
        summaryRefresh.setOnRefreshListener(listener);
        // Data Refresh Listener
        data.getQueue().addRequestFinishedListener(response -> {

            // Stop Refreshing
            dryerRefresh.setRefreshing(false);
            washerRefresh.setRefreshing(false);
            summaryRefresh.setRefreshing(false);

            if(data.getRoomData() != null){
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

            }

        });

        TextView quadNameText = (TextView) findViewById(R.id.txt_quadName);
        View machineMenu = findViewById(R.id.machine_menu); machineMenu.setTranslationY(50);

        refreshed = findViewById(R.id.network_status); refreshed.setTranslationY(-100); refreshed.setVisibility(View.GONE);

        TextView buildingNameText = (TextView) findViewById(R.id.txt_buildingName);
        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.btn_refresh);

        ImageView colorL = (ImageView) findViewById(R.id.img_highlightL);
        ImageView colorR = (ImageView) findViewById(R.id.img_highlightR);
        ImageView lineL = (ImageView) findViewById(R.id.line_left);
        ImageView lineR = (ImageView) findViewById(R.id.line_right);

        GeneralUI.changeStatusBarColor(getWindow(), quadColor);

        quadNameText.setText(quadName.toUpperCase());
        buildingNameText.setText(buildingName);

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

        data.getData();

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

        if(data.getFavorites().size() == 0)
            findViewById(R.id.img_notFound).setVisibility(View.VISIBLE);
        else{
            favoriteGrid.setVisibility(View.VISIBLE);
            findViewById(R.id.img_notFound).setVisibility(View.GONE);
        }

        Room roomData = data.getRoomData();
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

        Room roomData = data.getRoomData();
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

        favoriteAdapter.notifyDataSetChanged();
    }

    void showDryerData(){

        dryerGrid = (GridView) pager.findViewById(R.id.grid_dryers);

        // Setting up the dryerGrid view.

        dryerAdapter = new MachineGridStatusAdapter(context, data.getRoomData(), false);
        dryerGrid.setAdapter(dryerAdapter);
        dryerGrid.setColumnWidth(GridView.AUTO_FIT);
        dryerGrid.setNumColumns(GridView.AUTO_FIT);

    }

    void showFavoriteData(){
        favoriteGrid = pager.findViewById(R.id.grid_favoriteMachines);

        favoriteAdapter = new FavoriteGridStatusAdapter(context, data.getFavorites());
        favoriteGrid.setAdapter(favoriteAdapter);

        // Resizing grid.
        int numberOfMachines = data.getFavorites().size() / 2;
        numberOfMachines += ((data.getFavorites().size() % 2 == 0) ? 0 : 1);
        GeneralUI.resizeGridViewHeight(favoriteGrid, 200 * (numberOfMachines), context);

        favoriteGrid.setColumnWidth(GridView.AUTO_FIT);
        favoriteGrid.setNumColumns(GridView.AUTO_FIT);
    }

    void showWasherData(){

        washerGrid = (GridView) pager.findViewById(R.id.grid_washers);

//        washerGrid.setEnabled(false);

        // Setting up washer washerGrid view.
        washerAdapter = new MachineGridStatusAdapter(context, data.getRoomData(), true);
        washerGrid.setColumnWidth(GridView.AUTO_FIT);
        washerGrid.setNumColumns(GridView.AUTO_FIT);
        washerGrid.setAdapter(washerAdapter);
    }

    void onClick(View view){

        switch(view.getId()){

            case R.id.bg:
                View machineMenu = findViewById(R.id.machine_menu);
                Animations.hideDown(machineMenu);
                Animations.hide(findViewById(R.id.bg));
            break;

            case R.id.btn_favorite:

                TextView number = findViewById(R.id.txt_machineNumber);
                int machineNumber = Integer.parseInt(number.getText().toString());
                data.addMachineToFavorites(machineNumber);

                // Showing not found icon.
                if(data.getFavorites().size() == 0){
                    favoriteGrid.setVisibility(View.GONE);
                    findViewById(R.id.img_notFound).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.img_notFound).setVisibility(View.GONE);
                    favoriteGrid.setVisibility(View.VISIBLE);
                }

                // Resizing grid.
                int numberOfMachines = data.getFavorites().size() / 2;
                numberOfMachines += ((data.getFavorites().size() % 2 == 0) ? 0 : 1);
                GeneralUI.resizeGridViewHeight(favoriteGrid, 200 * (numberOfMachines), context);

                favoriteAdapter.notifyDataSetChanged();
                break;


            default: return;
        }

    }


}
