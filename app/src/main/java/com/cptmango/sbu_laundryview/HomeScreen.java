package com.cptmango.sbu_laundryview;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.cptmango.sbu_laundryview.adapters.MachineGridStatusAdapter;
import com.cptmango.sbu_laundryview.adapters.SelectQuadAdapter;
import com.cptmango.sbu_laundryview.data.DataManager;

import java.util.List;

public class HomeScreen extends AppCompatActivity {

    DataManager data;
    GridView grid;
    MachineGridStatusAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        startRoomPick();
        //connectToAPI();
    }

    void startRoomPick(){

        Intent intent = new Intent(this, SelectRoom.class);
        startActivity(intent);

    }

    void connectToAPI() {

        data = new DataManager(this, "Mendelsohn", "Irving");
        data.getData();

        data.getQueue().addRequestFinishedListener(response -> {

            if(data.getRoomData() != null) initializeUI();

        });

    }

    void initializeUI(){

        adapter = new MachineGridStatusAdapter(this, data.getRoomData(), true);

        grid = (GridView) findViewById(R.id.grid);
        grid.setColumnWidth(GridView.AUTO_FIT);
        grid.setNumColumns(GridView.AUTO_FIT);

        grid.setAdapter(adapter);

    }

    void updateData(){

        data.getData();

        data.getQueue().addRequestFinishedListener(response -> {

            if(data.getRoomData() != null) adapter.notifyDataSetChanged();

        });

    }

    void buttonClicked(View view){

        switch(view.getId()){

            case R.id.refresh: updateData();
                Toast.makeText(this, "Refreshing data.", Toast.LENGTH_SHORT).show();
            break;

            default: return;

        }



    }


}
