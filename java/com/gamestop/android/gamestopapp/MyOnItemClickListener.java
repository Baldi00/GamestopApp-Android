package com.gamestop.android.gamestopapp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

public class MyOnItemClickListener implements AdapterView.OnItemClickListener {

    private Context main;

    public MyOnItemClickListener(Context main){
        this.main = main;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Game g = (Game)parent.getItemAtPosition(position);

        Intent i = new Intent(view.getContext(),ActivityGamePage.class);
        i.putExtra("source","wishlist");
        i.putExtra("title",g.getTitle());
        i.putExtra("platform",g.getPlatform());
        i.putExtra("publisher",g.getPublisher());
        i.putExtra("cover",g.getCover());
        i.putExtra("newPrice",g.getNewPrice());
        i.putExtra("usedPrice",g.getUsedPrice());
        main.startActivity(i);
    }
}
