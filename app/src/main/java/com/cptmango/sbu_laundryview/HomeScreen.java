package com.cptmango.sbu_laundryview;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cptmango.sbu_laundryview.adapters.MachineStatusAdapter;
import com.cptmango.sbu_laundryview.data.DataManager;

public class HomeScreen extends AppCompatActivity {

    DataManager data;

    ListView list;
    MachineStatusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        connectToAPI();
    }

    void connectToAPI() {

        data = new DataManager(this, "Mendelsohn", "Irving");
        data.getData();

        data.getQueue().addRequestFinishedListener(response -> {

            if(data.getRoomData() != null) initializeUI();

        });

    }

    void initializeUI(){

        adapter = new MachineStatusAdapter(this, data.getRoomData(), true);

        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

    }



}
