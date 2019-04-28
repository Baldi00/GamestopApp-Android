package com.gamestop.android.gamestopapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class MyOnItemClickListener implements AdapterView.OnItemClickListener {

    private MainActivity main;
    private String source;

    public MyOnItemClickListener(MainActivity main, String source){
        this.main = main;
        this.source = source;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!main.isSelecting()) {
            Game g = (Game) parent.getItemAtPosition(position);

            Intent i = new Intent(view.getContext(), ActivityGamePage.class);
            i.putExtra("source", source);
            i.putExtra("title", g.getTitle());
            i.putExtra("platform", g.getPlatform());
            i.putExtra("publisher", g.getPublisher());
            i.putExtra("cover", g.getCover());
            i.putExtra("newPrice", g.getNewPrice());
            i.putExtra("usedPrice", g.getUsedPrice());
            main.startActivity(i);
        } else {

            //Get actual color
            int color = Color.TRANSPARENT;
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();

            if(color == Color.parseColor("#FBD2D0")) {
                view.setBackgroundColor(Color.parseColor("#FAFAFA"));
            }else{
                view.setBackgroundColor(Color.parseColor("#FBD2D0"));
            }
        }
    }
}
