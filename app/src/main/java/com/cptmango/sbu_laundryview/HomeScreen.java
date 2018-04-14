package com.cptmango.sbu_laundryview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_machine_status);

        TextView stony = (TextView) findViewById(R.id.stony);
        //stony.setLetterSpacing((float) 1.0);
    }
}
