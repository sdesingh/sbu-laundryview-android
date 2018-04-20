package com.cptmango.sbu_laundryview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.cptmango.sbu_laundryview.adapters.SelectQuadAdapter;

/**
 * Created by mango on 4/20/18.
 */

public class SelectRoom extends AppCompatActivity{

    ListView list;
    SelectQuadAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);

        initializeUI();
    }

    void initializeUI(){

        list = (ListView) findViewById(R.id.list_quadList);
        adapter = new SelectQuadAdapter(this);
        list.setAdapter(adapter);

    }



}
