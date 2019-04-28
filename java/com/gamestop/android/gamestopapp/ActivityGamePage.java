package com.gamestop.android.gamestopapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ActivityGamePage extends Activity {

    /*********************************************************************************************************************************/


    /*****************************************/
    /************   ATTRIBUTES   *************/
    /*****************************************/

    //GRAPHICS VIEWS

    //Game info
    private TextView titleHeader,title,platform,publisher,newPrice,oldNewPrice,usedPrice,oldUsedPrice,genres,releaseDate,numberOfPlayers,description;
    private ImageView cover;

    //Progress bar shown when searching
    private ProgressBar progressBarOnDownlaod;


    //OTHER ATTRIBUTES

    private Intent caller;

    private int version;

    /*********************************************************************************************************************************/


    /*****************************************/
    /************   CALLBACKS   **************/
    /*****************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //VERSION
        File versionFile = new File(getFilesDir(),"version.txt");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(versionFile));
            version = Integer.parseInt(bufferedReader.readLine());
            if(version == 1){
                setContentView(R.layout.activity_game_page_old);
            } else {
                setContentView(R.layout.activity_game_page);
            }
        }catch (Exception e){
            Toast.makeText(this,"Impossibile leggere file di configurazione",Toast.LENGTH_LONG).show();
            version = 2;
            setContentView(R.layout.activity_game_page);
        }

        //GET ALL VIEWS FROM APP GRAPHIC

        titleHeader = (TextView) findViewById(R.id.titleHeader);
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
        description = (TextView) findViewById(R.id.description);
        cover = (ImageView) findViewById(R.id.image);

        progressBarOnDownlaod = (ProgressBar) findViewById(R.id.progressBarOnDownlaod);

        //Get the intent
        caller = getIntent();

        usedPrice.setPaintFlags(usedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //If caller is a game from search list before download game
        /*if(((String)caller.getStringExtra("source")).equals("searchGameList")) {

            DownloadGamePage downloadGamePage = new DownloadGamePage(this);     //NOT IMPLEMENTED YET
            downloadGamePage.execute();

            findViewById(R.id.wholePage).setVisibility(View.GONE);
            progressBarOnDownlaod.setVisibility(View.VISIBLE);

            findViewById(R.id.remove).setVisibility(View.GONE);
            findViewById(R.id.add).setVisibility(View.VISIBLE);

        }*/
        //Else if caller is a game from wishlist immediatly set attributes
        //else {
            if(version==2) titleHeader.setText(caller.getStringExtra("title"));
            title.setText(caller.getStringExtra("title"));
            platform.setText(caller.getStringExtra("platform"));
            publisher.setText(caller.getStringExtra("publisher"));
            releaseDate.setText(caller.getStringExtra("releaseDate"));
            numberOfPlayers.setText(caller.getStringExtra("players"));
            description.setText(caller.getStringExtra("description"));

            genres.setText(caller.getStringArrayListExtra("genres").get(0));
            for(int i = 1; i<caller.getStringArrayListExtra("genres").size();i++){
                genres.setText(genres.getText().toString() + ", " + caller.getStringArrayListExtra("genres").get(i));
            }

            ArrayList<String> pegiData = caller.getStringArrayListExtra("pegi");
            for(int i=0;i<pegiData.size();i++){
                if(pegiData.get(i).equals("pegi18")) {
                    findViewById(R.id.pegi18).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("pegi16")) {
                    findViewById(R.id.pegi16).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("pegi12")) {
                    findViewById(R.id.pegi12).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("pegi7")) {
                    findViewById(R.id.pegi7).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("pegi3")) {
                    findViewById(R.id.pegi3).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("bad-language")) {
                    findViewById(R.id.pegiBadLanguage).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("violence")) {
                    findViewById(R.id.pegiViolence).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("online")) {
                    findViewById(R.id.pegiOnline).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("sex")) {
                    findViewById(R.id.pegiSex).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("fear")) {
                    findViewById(R.id.pegiFear).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("drugs")) {
                    findViewById(R.id.pegiDrugs).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("discrimination")) {
                    findViewById(R.id.pegiDiscrimination).setVisibility(View.VISIBLE);
                } else if(pegiData.get(i).equals("gambling")) {
                    findViewById(R.id.pegiGambling).setVisibility(View.VISIBLE);
                }
            }

            newPrice.setText(String.valueOf(caller.getDoubleExtra("newPrice",-1)) + "€");
            usedPrice.setText(String.valueOf(caller.getDoubleExtra("usedPrice",-1)) + "€");
            cover.setImageURI(Uri.fromFile(new File(caller.getStringExtra("cover"))));

            findViewById(R.id.add).setVisibility(View.GONE);
            findViewById(R.id.remove).setVisibility(View.VISIBLE);

        //}

        //Add images to gallery
        if(version == 2) {
            LinearLayout gallery = (LinearLayout) findViewById(R.id.gallery);
            File [] galleryImages = new File(caller.getStringExtra("galleryDir")).listFiles();
            for (int i = 0; i < galleryImages.length; i++) {
                ImageView galleryImage = new ImageView(getBaseContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                if (i == galleryImages.length-1) {
                    layoutParams.setMargins(10, 0, 10, 0);
                } else {
                    layoutParams.setMargins(10, 0, 0, 0);
                }
                galleryImage.setImageURI(Uri.fromFile(galleryImages[i]));
                galleryImage.setLayoutParams(layoutParams);
                galleryImage.setAdjustViewBounds(true);
                galleryImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enlargeImage(v);
                    }
                });
                gallery.addView(galleryImage);
            }
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
                        try {
                            MainActivity.addGameFromGamePage(new Game("https://www.gamestop.it/PC/Games/34054/gta-v",null));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),title.getText().toString()+" è stato aggiunto alla wishlist",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        finish();
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
                        try {
                            MainActivity.removeGameFromGamePage(new Game("https://www.gamestop.it/PC/Games/34054/gta-v",null));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),title.getText().toString()+" è stato rimosso dalla wishlist",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        finish();
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
                if (item.getTitle().toString().equals(getString(R.string.more_options_open_on_site))){
                    Toast.makeText(getApplicationContext(),"Non ancora implementato",Toast.LENGTH_SHORT).show();
                }else if(item.getTitle().toString().equals(getString(R.string.more_options_settings))){
                    Intent i = new Intent(getApplicationContext(), ActivitySettings.class);
                    startActivity(i);
                }
                return false;
            }
        });

        popup.show();
    }

    public void enlargeImage(View v){
        ImageView caller = (ImageView)v;
        Dialog imageDialog = new Dialog(this);
        imageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(getLayoutInflater().inflate(R.layout.enlarged_image_dialog, null));

        ImageView imageView = (ImageView)imageDialog.findViewById(R.id.image);
        imageView.setImageDrawable(caller.getDrawable());
        imageDialog.show();
    }

}
