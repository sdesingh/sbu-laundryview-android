package com.cptmango.sbu_laundryview.ui;

import android.view.View;

public class Animations {

    public static void hide(View view){

        view.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() ->  view.setVisibility(View.GONE));


    }

    public static void hideDown(View view){

        view.animate()
                .alpha(0f)
                .setDuration(250)
                .translationY(100)
                .withEndAction(() ->  view.setVisibility(View.GONE));
    }

    public static void show(View view){

        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .setDuration(300);

    }

    public static void show(View view, float alpha){

        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(alpha)
                .setDuration(300);

    }

    public static void showUp(View view){

        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(250);
    }


}
