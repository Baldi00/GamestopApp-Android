package com.gamestop.android.gamestopapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class ActivityGamePage extends Activity {

    private TextView title,platform,publisher,newPrice,oldNewPrice,usedPrice,oldUsedPrice,genres,releaseDate,numberOfPlayers;
    private ImageView cover;

    private ProgressBar progressBarOnDownlaod;

    private Intent caller;

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

        progressBarOnDownlaod = (ProgressBar) findViewById(R.id.progressBarOnDownlaod);

        caller = getIntent();

        if(((String)caller.getStringExtra("source")).equals("searchGameList")) {
            DownloadGamePage downloadGamePage = new DownloadGamePage(this);
            downloadGamePage.execute();

            findViewById(R.id.wholePage).setVisibility(View.GONE);
            progressBarOnDownlaod.setVisibility(View.VISIBLE);

            findViewById(R.id.remove).setVisibility(View.GONE);
            findViewById(R.id.add).setVisibility(View.VISIBLE);
        } else {
            title.setText(caller.getStringExtra("title"));
            platform.setText(caller.getStringExtra("platform"));
            publisher.setText(caller.getStringExtra("publisher"));
            newPrice.setText(String.valueOf(caller.getDoubleExtra("newPrice",-1)) + "€");
            usedPrice.setText(String.valueOf(caller.getDoubleExtra("usedPrice",-1)) + "€");
            findViewById(R.id.add).setVisibility(View.GONE);
            findViewById(R.id.remove).setVisibility(View.VISIBLE);

            String coverPath = caller.getStringExtra("cover");
            Context context = cover.getContext();
            int id = context.getResources().getIdentifier(coverPath.substring(0,coverPath.lastIndexOf(".")), "drawable", context.getPackageName());
            cover.setImageResource(id);
        }



    }

    public void onEndDownload(Object result){
        progressBarOnDownlaod.setVisibility(View.GONE);
        findViewById(R.id.wholePage).setVisibility(View.VISIBLE);

        title.setText(caller.getStringExtra("title"));
        platform.setText(caller.getStringExtra("platform"));
        publisher.setText(caller.getStringExtra("publisher"));
        newPrice.setText(String.valueOf(caller.getDoubleExtra("newPrice",-1)) + "€");
        usedPrice.setText(String.valueOf(caller.getDoubleExtra("usedPrice",-1)) + "€");

        String coverPath = caller.getStringExtra("cover");
        Context context = cover.getContext();
        int id = context.getResources().getIdentifier(coverPath.substring(0,coverPath.lastIndexOf(".")), "drawable", context.getPackageName());
        cover.setImageResource(id);
    }

    //MENU TOOLBAR

    public void addToList(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.AlertDialogActivityGamePage);
        dialog.setMessage("Vuoi aggiungere " + title.getText().toString() + " alla wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        dialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    public void removeFromList(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.AlertDialogActivityGamePage);
        dialog.setMessage("Vuoi rimuovere " + title.getText().toString() + " dalla wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        dialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

}
