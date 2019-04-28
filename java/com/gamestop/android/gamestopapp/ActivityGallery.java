package com.gamestop.android.gamestopapp;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActivityGallery extends AppCompatActivity {

    ViewPager viewPager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final Intent caller = getIntent();
        String [] images = caller.getStringArrayExtra("images");

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(ActivityGallery.this,images);
        viewPager.setAdapter(adapter);

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(caller.getIntExtra("position",0));
            }
        });
    }

}
