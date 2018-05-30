package com.cptmango.sbu_laundryview.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cptmango.sbu_laundryview.fragments.DryerStatus;
import com.cptmango.sbu_laundryview.fragments.LaundrySummary;
import com.cptmango.sbu_laundryview.fragments.WasherStatus;

public class HomeScreenFragmentPagerAdapter extends FragmentPagerAdapter {

    Context context;
    final int NUM_ITEMS = 3;


    public HomeScreenFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){

            case 0: return new WasherStatus();

            case 1: return new LaundrySummary();

            case 2: return new DryerStatus();

        }

        return new LaundrySummary();

    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
