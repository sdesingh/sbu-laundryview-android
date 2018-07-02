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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.adapters.HomeScreenFragmentPagerAdapter;
import com.cptmango.sbu_laundryview.adapters.MachineGridStatusAdapter;
import com.cptmango.sbu_laundryview.data.DataManager;
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
        settings.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(quadColor)));
        colorL.setColorFilter(Color.parseColor(quadColor));
        colorR.setColorFilter(Color.parseColor(quadColor));
        lineL.setColorFilter(Color.parseColor(quadColor));
        lineR.setColorFilter(Color.parseColor(quadColor));
    }

    void updateData(){

        data.getData();

        data.getQueue().addRequestFinishedListener(response -> {

            if(data.getRoomData() != null){
                washerAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Refreshed successfully.", Toast.LENGTH_SHORT).show();
            }

        });

    }

    void buttonClicked(View view){

        switch(view.getId()){

            case R.id.btn_refresh: updateData();
                Toast.makeText(this, "Refreshing data.", Toast.LENGTH_SHORT).show();
            break;

            default: return;

        }



    }

    void showDryerData(){

        dryerGrid = (GridView) pager.findViewById(R.id.grid_dryers);

//        dryerGrid.setEnabled(false);

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


}
