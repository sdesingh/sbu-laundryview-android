package com.cptmango.sbu_laundryview.ui;

import android.view.View;

public class Animations {

    public static void hide(View view){

        view.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() ->  view.setVisibility(View.GONE));


    }

    public static void show(View view){

        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .setDuration(300);

    }



}
