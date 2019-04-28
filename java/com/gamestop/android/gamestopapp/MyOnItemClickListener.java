package com.gamestop.android.gamestopapp;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

public class MyOnItemClickListener implements AdapterView.OnItemClickListener {

    private ActivityMain main;
    private String source;

    public MyOnItemClickListener(ActivityMain main, String source){
        this.main = main;
        this.source = source;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GamePreview g = (GamePreview) parent.getItemAtPosition(position);

        Intent i = new Intent(view.getContext(), ActivityGamePage.class);
        i.putExtra("source",source);

        if(source.equals("wishlist")) {
            i.putExtra("id",g.getId());
        }
        else if(source.equals("search")){
            i.putExtra("url", g.getURL());
        }

        main.startActivity(i);
    }
}
