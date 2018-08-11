package com.cptmango.sbu_laundryview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cptmango.sbu_laundryview.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onClick(View view){

        switch(view.getId()){
            case R.id.btn_back: finish();
            break;

            case R.id.setting_changeRoom:
                changeRoom();
            break;
        }

    }

    public void changeRoom(){
        setResult(RESULT_OK);

        Intent intent = new Intent(this, SelectRoom.class);
        startActivityForResult(intent, 1);

    }
}
