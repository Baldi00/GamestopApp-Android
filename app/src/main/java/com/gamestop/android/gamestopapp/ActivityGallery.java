package com.gamestop.android.gamestopapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

public class ActivityGallery extends Activity {

    ViewPager viewPager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final Intent caller = getIntent();
        String [] images = caller.getStringArrayExtra("images");

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(ActivityGallery.this,images);
        viewPager.setAdapter(adapter);

        if(savedInstanceState==null) {
            viewPager.setCurrentItem(caller.getIntExtra("position", 0));
        }else{
            viewPager.setCurrentItem(savedInstanceState.getInt("position"));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("position",viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }
}
