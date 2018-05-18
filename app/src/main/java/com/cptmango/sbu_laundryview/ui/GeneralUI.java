package com.cptmango.sbu_laundryview.ui;

import android.graphics.Color;
import android.view.Window;
import android.view.WindowManager;

public class GeneralUI {

    public static void changeStatusBarColor(Window window, String color){

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(Color.parseColor(color));


    }



}
