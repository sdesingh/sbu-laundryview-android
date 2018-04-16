package com.cptmango.sbu_laundryview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cptmango.sbu_laundryview.data.DataManager;

public class HomeScreen extends AppCompatActivity {

    DataManager data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_machine_status);

        data = new DataManager(this, "Mendelsohn", "Irving");
        data.getData();


    }
}
