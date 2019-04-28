package com.gamestop.android.gamestopapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class ActivityGamePage extends Activity {

    /*********************************************************************************************************************************/


    /*****************************************/
    /************   ATTRIBUTES   *************/
    /*****************************************/

    //GRAPHICS VIEWS

    //Game info
    private TextView title,platform,publisher,newPrice,oldNewPrice,usedPrice,oldUsedPrice,genres,releaseDate,numberOfPlayers;
    private ImageView cover;

    //Progress bar shown when searching
    private ProgressBar progressBarOnDownlaod;


    //OTHER ATTRIBUTES

    private Intent caller;

    /*********************************************************************************************************************************/


    /*****************************************/
    /************   CALLBACKS   **************/
    /*****************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        //GET ALL VIEWS FROM APP GRAPHIC

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

        //Get the intent
        caller = getIntent();

        usedPrice.setPaintFlags(usedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //If caller is a game from search list before download game
        if(((String)caller.getStringExtra("source")).equals("searchGameList")) {

            DownloadGamePage downloadGamePage = new DownloadGamePage(this);     //NOT IMPLEMENTED YET
            downloadGamePage.execute();

            findViewById(R.id.wholePage).setVisibility(View.GONE);
            progressBarOnDownlaod.setVisibility(View.VISIBLE);

            findViewById(R.id.remove).setVisibility(View.GONE);
            findViewById(R.id.add).setVisibility(View.VISIBLE);

        }
        //Else if caller is a game from wishlist immediatly set attributes
        else {
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

    //If caller is a game from search list, after download set attributes
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


    /*********************************************************************************************************************************/


    /*****************************************/
    /**********   OTHER METHODS   ************/
    /*****************************************/

    //Add the game into wishlist if caller is a game from search list
    public void addToList(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.DialogActivityGamePage);
        dialog.setMessage("Vuoi aggiungere " + title.getText().toString() + " alla wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO Add game into wishlist
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

    //Remove the game from wishlist if caller is a game from wishlist
    public void removeFromList(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.DialogActivityGamePage);
        dialog.setMessage("Vuoi rimuovere " + title.getText().toString() + " dalla wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO Remove game from wishlist
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

    //Show the menu on more options button
    public void showMoreOptionsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.more_options_menu_activity_game_page, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(),"Non ancora implementato",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        popup.show();
    }

}
