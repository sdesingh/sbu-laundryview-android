package com.cptmango.sbu_laundryview;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cptmango.sbu_laundryview.adapters.MachineGridStatusAdapter;
import com.cptmango.sbu_laundryview.data.DataManager;
import com.cptmango.sbu_laundryview.ui.GeneralUI;

import org.w3c.dom.Text;

public class HomeScreen extends AppCompatActivity {

    DataManager data;
    GridView grid;
    MachineGridStatusAdapter adapter;

    String quadName;
    String buildingName;
    String quadColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initialCheck();
    }

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

        adapter = new MachineGridStatusAdapter(this, data.getRoomData(), true);

        grid = (GridView) findViewById(R.id.grid_washers);
        grid.setColumnWidth(GridView.AUTO_FIT);
        grid.setNumColumns(GridView.AUTO_FIT);

        grid.setAdapter(adapter);

        TextView quadNameText = (TextView) findViewById(R.id.txt_quadName);
        TextView buildingNameText = (TextView) findViewById(R.id.txt_buildingName);
        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.btn_refresh);
        ImageView colorL = (ImageView) findViewById(R.id.img_highlightL);
        ImageView colorR = (ImageView) findViewById(R.id.img_highlightR);

        GeneralUI.changeStatusBarColor(getWindow(), quadColor);

        quadNameText.setText(quadName.toUpperCase());
        buildingNameText.setText(buildingName);
        refresh.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(quadColor)));
        colorL.setColorFilter(Color.parseColor(quadColor));
        colorR.setColorFilter(Color.parseColor(quadColor));
    }

    void updateData(){

        data.getData();

        data.getQueue().addRequestFinishedListener(response -> {

            if(data.getRoomData() != null) adapter.notifyDataSetChanged();

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


}
