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
import android.widget.TextView;
import android.widget.Toast;

import com.cptmango.sbu_laundryview.R;
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
    ViewPager pager;
    MachineGridStatusAdapter washerAdapter;
    MachineGridStatusAdapter dryerAdapter;
    HomeScreenFragmentPagerAdapter pagerAdapter;

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

    void connectToAPI() {

        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        quadName = prefs.getString("quad", "Mendelsohn");
        buildingName = prefs.getString("building", "Irving");
        quadColor = prefs.getString("quadColor", "000000");

        data = new DataManager(this, quadName, buildingName);
        data.getData();

        data.getQueue().addRequestFinishedListener(response -> {

            if(data.getRoomData() != null) initializeUI();

        });

    }

    void initializeUI(){

        //Setting up view pager.
        pagerAdapter = new HomeScreenFragmentPagerAdapter(getSupportFragmentManager(), this);
        pager = (ViewPager) findViewById(R.id.pager_HomeScreen);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1);
        pager.setOffscreenPageLimit(3);
        showWasherData();
        showDryerData();
        showSummaryPage();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_summary);

        //Bottom Tab Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
            }
        });

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
                else{
                    bottomNavigationView.setSelectedItemId(R.id.nav_summary);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        TextView quadNameText = (TextView) findViewById(R.id.txt_quadName);
        SwipeRefreshLayout dryerRefresh = (SwipeRefreshLayout) findViewById(R.id.tab_dryers);
        SwipeRefreshLayout washerRefresh = (SwipeRefreshLayout) findViewById(R.id.tab_washers);
        dryerRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
        washerRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
        View refreshed = findViewById(R.id.network_status); refreshed.setTranslationY(-100); refreshed.setVisibility(View.GONE);
        TextView buildingNameText = (TextView) findViewById(R.id.txt_buildingName);
        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.btn_refresh);
        FloatingActionButton settings = (FloatingActionButton) findViewById(R.id.btn_settings);
        ImageView colorL = (ImageView) findViewById(R.id.img_highlightL);
        ImageView colorR = (ImageView) findViewById(R.id.img_highlightR);
        ImageView lineL = (ImageView) findViewById(R.id.line_left);
        ImageView lineR = (ImageView) findViewById(R.id.line_right);

        GeneralUI.changeStatusBarColor(getWindow(), quadColor);

        quadNameText.setText(quadName.toUpperCase());
        buildingNameText.setText(buildingName);

        // Changing the color of UI elements to match the quad's color.
        refresh.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(quadColor)));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Animation expand = AnimationUtils.loadAnimation(context, R.anim.expand_item);
//                expand.setDuration(500);
//                refreshed.startAnimation(expand);
                updateData();

            }
        });
        settings.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(quadColor)));
        colorL.setColorFilter(Color.parseColor(quadColor));
        colorR.setColorFilter(Color.parseColor(quadColor));
        lineL.setColorFilter(Color.parseColor(quadColor));
        lineR.setColorFilter(Color.parseColor(quadColor));
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

    void updateData(){

        data.getData();

        data.getQueue().addRequestFinishedListener(response -> {

            if(data.getRoomData() != null){
                View refreshed = findViewById(R.id.network_status);
                refreshed.setVisibility(View.VISIBLE);
                refreshed.animate().translationY(0).setDuration(200).setInterpolator(new LinearInterpolator()).withEndAction(() -> {
                    refreshed.animate().translationY(-50).setDuration(300).setInterpolator(new LinearInterpolator()).setStartDelay(1000)
                            .withEndAction(() -> {refreshed.setVisibility(View.GONE);});

                });

                washerAdapter.notifyDataSetChanged();
                dryerAdapter.notifyDataSetChanged();


            }

        });

    }

    void showSummaryPage(){
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






    }

    void showDryerData(){

        dryerGrid = (GridView) pager.findViewById(R.id.grid_dryers);

        // Setting up the dryerGrid view.

        dryerAdapter = new MachineGridStatusAdapter(context, data.getRoomData(), false);
        dryerGrid.setAdapter(dryerAdapter);
        dryerGrid.setColumnWidth(GridView.AUTO_FIT);
        dryerGrid.setNumColumns(GridView.AUTO_FIT);

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
                Animations.hide(machineMenu);
            default: return;
        }

    }


}
