package com.gamestop.android.gamestopapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class GamePage extends Activity {

    private TextView title,platform,publisher,newPrice,oldNewPrice,usedPrice,oldUsedPrice,genres,releaseDate,numberOfPlayers;
    private ImageView cover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        title = (TextView) findViewById(R.id.title);
        platform = (TextView) findViewById(R.id.platform);
        publisher = (TextView) findViewById(R.id.publisher);
        newPrice = (TextView) findViewById(R.id.newPrice);
        oldNewPrice = (TextView) findViewById(R.id.oldNewPrice);
        usedPrice = (TextView) findViewById(R.id.usedPrice);
        oldUsedPrice = (TextView) findViewById(R.id.oldUsedPrice);
        genres = (TextView) findViewById(R.id.genres);
        releaseDate = (TextView) findViewById(R.id.releaseDate);
        numberOfPlayers = (TextView) findViewById(R.id.players);
        cover = (ImageView) findViewById(R.id.image);

        Intent caller = getIntent();

        title.setText(caller.getStringExtra("title"));
        platform.setText(caller.getStringExtra("platform"));
        publisher.setText(caller.getStringExtra("publisher"));
        newPrice.setText(String.valueOf(caller.getDoubleExtra("newPrice",-1)));
        usedPrice.setText(String.valueOf(caller.getDoubleExtra("usedPrice",-1)));

        String coverPath = caller.getStringExtra("cover");
        Context context = cover.getContext();
        int id = context.getResources().getIdentifier(coverPath.substring(0,coverPath.lastIndexOf(".")), "drawable", context.getPackageName());
        cover.setImageResource(id);

    }
}
