package com.gamestop.android.gamestopapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class MyOnItemClickListener implements AdapterView.OnItemClickListener {

    private MainActivity main;
    private String source;

    public MyOnItemClickListener(MainActivity main, String source){
        this.main = main;
        this.source = source;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GamePreview g = (GamePreview) parent.getItemAtPosition(position);

        Intent i = new Intent(view.getContext(), ActivityGamePage.class);
        i.putExtra("url",g.getURL());

        main.startActivity(i);
    }
}
