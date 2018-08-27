package com.cptmango.sbu_laundryview.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

public class UI_Utilities {

    public static void changeStatusBarColor(Window window, String color){

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(Color.parseColor(color));


    }

    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    public static void resizeGridViewHeight(GridView grid, float height, Context context){
        ViewGroup.LayoutParams layoutParams = grid.getLayoutParams();
        layoutParams.height = convertDpToPixels(height, context);
        grid.setLayoutParams(layoutParams);
    }

    public static void resizeView(View view, int width, int height){

        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;

    }

}
