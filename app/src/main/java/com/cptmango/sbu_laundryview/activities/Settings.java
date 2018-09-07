package com.cptmango.sbu_laundryview.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.data.DataManager;

public class Settings extends AppCompatActivity{

    // Used to fix a weird bug in Android with the selection of the drop down menu.
    private boolean created = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner spinner = findViewById(R.id.drop_reminder);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.reminder_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int reminderTime = PreferenceManager.getDefaultSharedPreferences(this).getInt("reminder", 5);
        spinner.setSelection(5 - reminderTime, false);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!created){
                    created = true;
                }else {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();

                    int reminderTime = 5 - position;

                    editor.putInt("reminder", reminderTime);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Reminder time set.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner.setOnItemSelectedListener(listener);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                finish();
            }else{

            }

        }
    }

    public void changeRoom(){
        Log.i("LOG", "User is changing the room.");
        setResult(RESULT_OK);
        Intent intent = new Intent(this, SelectRoom.class);
        startActivityForResult(intent, 1);
    }


}
