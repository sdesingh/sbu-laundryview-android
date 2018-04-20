package com.cptmango.sbu_laundryview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.cptmango.sbu_laundryview.adapters.SelectQuadAdapter;

/**
 * Created by mango on 4/20/18.
 */

public class SelectRoom extends AppCompatActivity{

    ListView quadList;
    SelectQuadAdapter quadAdapter;
    View roomSelectMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);

        initializeUI();
    }

    void initializeUI(){

        roomSelectMenu = findViewById(R.id.menu_roomSelect);
        quadList = (ListView) findViewById(R.id.list_quadList);
        quadAdapter = new SelectQuadAdapter(this, roomSelectMenu);
        quadList.setAdapter(quadAdapter);

    }




}
