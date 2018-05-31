package com.cptmango.sbu_laundryview.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ListView;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.adapters.SelectQuadAdapter;
import com.cptmango.sbu_laundryview.ui.Animations;

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

    /**
     * Initial setup of the UI.
     */
    void initializeUI(){

        roomSelectMenu = findViewById(R.id.menu_roomSelect);
        quadList = (ListView) findViewById(R.id.list_quadList);

        quadAdapter = new SelectQuadAdapter(this, roomSelectMenu, findViewById(R.id.img_darkMenu));
        quadList.setAdapter(quadAdapter);

    }

    public void onButtonClick(View view){

        switch(view.getId()){


            case R.id.btn_closeRoomMenu: closeRoomMenu();
            break;

            // If the user clicks away from the room select menu.
            case R.id.img_darkMenu: closeRoomMenu();
            break;

            case R.id.btn_selectRoom: setResult(RESULT_OK); roomSelected();
            break;

        }


    }

    public void closeRoomMenu(){

        View roomMenu = findViewById(R.id.menu_roomSelect);
        View menuDark = findViewById(R.id.img_darkMenu);
        Animations.hide(roomMenu);
        Animations.hide(menuDark);

    }

    public void roomSelected(){
        finish();
    }

}
