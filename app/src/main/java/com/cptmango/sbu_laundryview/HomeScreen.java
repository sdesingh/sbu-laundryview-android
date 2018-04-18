package com.cptmango.sbu_laundryview;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.cptmango.sbu_laundryview.adapters.MachineGridStatusAdapter;
import com.cptmango.sbu_laundryview.data.DataManager;

public class HomeScreen extends AppCompatActivity {

    DataManager data;
    GridView grid;
    MachineGridStatusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        connectToAPI();
    }

    void connectToAPI() {

        data = new DataManager(this, "Roth", "Cardozo");
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


}
