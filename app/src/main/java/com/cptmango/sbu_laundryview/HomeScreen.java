package com.cptmango.sbu_laundryview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

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

        data = new DataManager(this, "Mendelsohn", "Irving");
        data.getData();

        adapter = new MachineStatusAdapter(this, data.getRoom(), true);

        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

    }
}
