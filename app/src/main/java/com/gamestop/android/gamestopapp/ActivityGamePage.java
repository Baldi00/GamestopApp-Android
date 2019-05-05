package com.gamestop.android.gamestopapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class ActivityGamePage extends Activity {

    /*********************************************************************************************************************************/


    /*****************************************/
    /************   ATTRIBUTES   *************/
    /*****************************************/

    //GRAPHICS VIEWS

    //Game info
    private TextView titleHeader,title,platform,publisher,newPrice,oldNewPrice,usedPrice,oldUsedPrice,digitalPrice,preorderPrice,genres,releaseDate,numberOfPlayers,officialSite,description;
    private ImageView cover;

    //Progress bar shown when searching
    private ProgressBar progressBarOnDownlaod;

    private Game gameOfThePage;


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

        titleHeader = (TextView) findViewById(R.id.titleHeader);
        title = (TextView) findViewById(R.id.title);
        platform = (TextView) findViewById(R.id.platform);
        publisher = (TextView) findViewById(R.id.publisher);
        newPrice = (TextView) findViewById(R.id.newPrice);
        oldNewPrice = (TextView) findViewById(R.id.oldNewPrice);
        usedPrice = (TextView) findViewById(R.id.usedPrice);
        oldUsedPrice = (TextView) findViewById(R.id.oldUsedPrice);
        digitalPrice = (TextView) findViewById(R.id.digitalPrice);
        preorderPrice = (TextView) findViewById(R.id.preorderPrice);
        genres = (TextView) findViewById(R.id.genres);
        releaseDate = (TextView) findViewById(R.id.releaseDate);
        numberOfPlayers = (TextView) findViewById(R.id.players);
        officialSite = (TextView) findViewById(R.id.officialSite);
        description = (TextView) findViewById(R.id.description);
        cover = (ImageView) findViewById(R.id.image);

        progressBarOnDownlaod = (ProgressBar) findViewById(R.id.progressBarOnDownlaod);

        findViewById(R.id.remove).setVisibility(View.GONE);
        findViewById(R.id.add).setVisibility(View.GONE);

        oldNewPrice.setPaintFlags(usedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        oldUsedPrice.setPaintFlags(usedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //Get the intent
        caller = getIntent();

        String source = caller.getStringExtra("source");

        if(source.equals("search") || source.equals("toAdd")) {
            progressBarOnDownlaod.setVisibility(View.VISIBLE);
            findViewById(R.id.wholePage).setVisibility(View.GONE);

            titleHeader.setText("Downloading...");
            Downloader downloader = new Downloader(this, caller.getStringExtra("url"));
            downloader.execute();

        }else if(source.equals("wishlist") || source.equals("notification")){
            findViewById(R.id.remove).setVisibility(View.VISIBLE);
            findViewById(R.id.wholePage).setVisibility(View.VISIBLE);

            try {
                gameOfThePage = DirectoryManager.importGame(caller.getStringExtra("id"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(gameOfThePage!=null)
                setGameOfThePageGraphic();
        }
    }


    /*********************************************************************************************************************************/


    /*****************************************/
    /**********   OTHER METHODS   ************/
    /*****************************************/

    //Add the game into wishlist if caller is a game from search list
    public void addToWishist(View v){
        final String titolo = title.getText().toString();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.DialogActivityGamePage);
        dialog.setMessage("Vuoi aggiungere " + titolo + " alla wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityMain.addToWishlistGamePage(gameOfThePage);

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
    public void removeFromWishlist(View v){
        final String titolo = title.getText().toString();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.DialogActivityGamePage);
        dialog.setMessage("Vuoi rimuovere " + titolo + " dalla wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityMain.removeFromWishlistGamePage(gameOfThePage);
                        Toast.makeText(getApplicationContext(),titolo + " è stato rimosso dalla wishlist",Toast.LENGTH_SHORT).show();
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

    //If caller is a game from search list, after download set attributes
    public void onEndDownload(Object result){
        gameOfThePage = (Game)result;

        if(caller.getStringExtra("source").equals("toAdd")){
            ActivityMain.addToWishlistGamePage(gameOfThePage);

            finish();
            return;
        }

        setGameOfThePageGraphic();
        progressBarOnDownlaod.setVisibility(View.GONE);
        findViewById(R.id.wholePage).setVisibility(View.VISIBLE);
        findViewById(R.id.add).setVisibility(View.VISIBLE);
    }

    //Set all informations and images in the page
    public void setGameOfThePageGraphic(){
        if(gameOfThePage==null){
            Toast.makeText(this,"Non sei connesso a internet",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        titleHeader.setText(gameOfThePage.getTitle());

        if(gameOfThePage.getTitle()!=null && !gameOfThePage.getTitle().equals(""))
            title.setText(gameOfThePage.getTitle().substring(0,gameOfThePage.getTitle().length()-2));
        else
            findViewById(R.id.titleLayout).setVisibility(View.GONE);

        if(gameOfThePage.getPlatform()!=null && !gameOfThePage.getPlatform().equals(""))
            platform.setText(gameOfThePage.getPlatform());
        else
            findViewById(R.id.platformLayout).setVisibility(View.GONE);

        if(gameOfThePage.getPublisher()!=null && !gameOfThePage.getPublisher().equals(""))
            publisher.setText(gameOfThePage.getPublisher());
        else
            findViewById(R.id.publisherLayout).setVisibility(View.GONE);

        if(gameOfThePage.getReleaseDate()!=null && !gameOfThePage.getReleaseDate().equals(""))
            releaseDate.setText(gameOfThePage.getReleaseDate());
        else
            findViewById(R.id.releaseDateLayout).setVisibility(View.GONE);

        if(gameOfThePage.getPlayers()!=null && !gameOfThePage.getPlayers().equals(""))
            numberOfPlayers.setText(gameOfThePage.getPlayers());
        else
            findViewById(R.id.playersLayout).setVisibility(View.GONE);

        if(gameOfThePage.getOfficialSite()!=null && !gameOfThePage.getOfficialSite().equals(""))
            officialSite.setText(gameOfThePage.getOfficialSite());
        else
            findViewById(R.id.officialSiteLayout).setVisibility(View.GONE);

        if(gameOfThePage.getDescription()!=null && !gameOfThePage.getDescription().equals(""))
            description.setText(gameOfThePage.getDescription());
        else
            findViewById(R.id.descriptionLayout).setVisibility(View.GONE);

        if(gameOfThePage.isValidForPromotions()){
            findViewById(R.id.validForPromotions).setVisibility(View.VISIBLE);
        }

        if(gameOfThePage.hasPromo()){
            findViewById(R.id.promoLayout).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.promoHeader)).setText(gameOfThePage.getPromo().get(0).getHeader());
            ((TextView)findViewById(R.id.promoValidity)).setText(gameOfThePage.getPromo().get(0).getValidity());
            ((TextView)findViewById(R.id.promoMessage)).setText(gameOfThePage.getPromo().get(0).getMessage());
        }

        if(gameOfThePage.getGenres()!=null && gameOfThePage.getGenres().size()>0) {
            genres.setText(gameOfThePage.getGenres().get(0));
            for (int i = 1; i < gameOfThePage.getGenres().size(); i++) {
                genres.setText(genres.getText().toString() + ", " + gameOfThePage.getGenres().get(i));
            }
        }else{
            findViewById(R.id.genresLayout).setVisibility(View.GONE);
        }

        List<String> pegiData = gameOfThePage.getPegi();
        if(pegiData!=null && pegiData.size()>0) {
            for (int i = 0; i < pegiData.size(); i++) {
                if (pegiData.get(i).equals("pegi18")) {
                    findViewById(R.id.pegi18).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("pegi16")) {
                    findViewById(R.id.pegi16).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("pegi12")) {
                    findViewById(R.id.pegi12).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("pegi7")) {
                    findViewById(R.id.pegi7).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("pegi3")) {
                    findViewById(R.id.pegi3).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("bad-language")) {
                    findViewById(R.id.pegiBadLanguage).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("violence")) {
                    findViewById(R.id.pegiViolence).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("online")) {
                    findViewById(R.id.pegiOnline).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("sex")) {
                    findViewById(R.id.pegiSex).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("fear")) {
                    findViewById(R.id.pegiFear).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("drugs")) {
                    findViewById(R.id.pegiDrugs).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("discrimination")) {
                    findViewById(R.id.pegiDiscrimination).setVisibility(View.VISIBLE);
                } else if (pegiData.get(i).equals("gambling")) {
                    findViewById(R.id.pegiGambling).setVisibility(View.VISIBLE);
                }
            }
        }else{
            findViewById(R.id.pegiLayout).setVisibility(View.GONE);
        }

        DecimalFormat df = new DecimalFormat("#.00");

        if(!String.valueOf(gameOfThePage.getPreorderPrice()).equals("null")){
            preorderPrice.setText(String.valueOf(df.format(gameOfThePage.getPreorderPrice())) + "€");
            findViewById(R.id.preorderPriceLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.newPriceLayout).setVisibility(View.GONE);
            findViewById(R.id.usedPriceLayout).setVisibility(View.GONE);
        } else if(!String.valueOf(gameOfThePage.getDigitalPrice()).equals("null")){
            digitalPrice.setText(String.valueOf(df.format(gameOfThePage.getDigitalPrice())) + "€");
            findViewById(R.id.digitalPriceLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.newPriceLayout).setVisibility(View.GONE);
            findViewById(R.id.usedPriceLayout).setVisibility(View.GONE);
        } else {
            if (!String.valueOf(gameOfThePage.getNewPrice()).equals("null"))
                newPrice.setText(String.valueOf(df.format(gameOfThePage.getNewPrice())) + "€");
            else
                findViewById(R.id.newPriceLayout).setVisibility(View.GONE);

            if (!String.valueOf(gameOfThePage.getUsedPrice()).equals("null"))
                usedPrice.setText(String.valueOf(df.format(gameOfThePage.getUsedPrice())) + "€");
            else
                findViewById(R.id.usedPriceLayout).setVisibility(View.GONE);
        }

        if(gameOfThePage.getOlderNewPrices()!=null && gameOfThePage.getOlderNewPrices().size()>0){
            oldNewPrice.setText(String.valueOf(df.format(gameOfThePage.getOlderNewPrices().get(0))) + "€");
            for(int i=1;i<gameOfThePage.getOlderNewPrices().size();i++){
                oldNewPrice.setText(oldNewPrice.getText().toString() + ", " + String.valueOf(df.format(gameOfThePage.getOlderNewPrices().get(i))) + "€");
            }
        }

        if(gameOfThePage.getOlderUsedPrices()!=null && gameOfThePage.getOlderUsedPrices().size()>0){
            oldUsedPrice.setText(String.valueOf(df.format(gameOfThePage.getOlderUsedPrices().get(0))) + "€");
            for(int i=1;i<gameOfThePage.getOlderUsedPrices().size();i++){
                oldUsedPrice.setText(oldUsedPrice.getText().toString() + ", " + String.valueOf(df.format(gameOfThePage.getOlderUsedPrices().get(i))) + "€");
            }
        }

        cover.setImageURI(Uri.fromFile(new File(gameOfThePage.getCover())));

        //Add images to gallery
        LinearLayout gallery = (LinearLayout) findViewById(R.id.gallery);
        File [] galleryImages = new File(DirectoryManager.getGameGalleryDirectory(gameOfThePage.getId())).listFiles();
        if(galleryImages!=null && galleryImages.length>0) {
            for (int i = 0; i < galleryImages.length; i++) {
                ImageView galleryImage = new ImageView(getBaseContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                int marginInPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                int marginInPixelRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
                if (i == galleryImages.length - 1) {
                    layoutParams.setMargins(marginInPixel, 0, marginInPixelRight, 0);
                } else {
                    layoutParams.setMargins(marginInPixel, 0, 0, 0);
                }
                galleryImage.setTag(i);
                galleryImage.setImageURI(Uri.fromFile(galleryImages[i]));
                galleryImage.setLayoutParams(layoutParams);
                galleryImage.setAdjustViewBounds(true);
                galleryImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery(v);
                    }
                });
                gallery.addView(galleryImage);
            }
        }else{
            findViewById(R.id.galleryLayout).setVisibility(View.GONE);
        }
    }

    //Open the gallery when an image is clicked
    public void openGallery(View v){

        File [] galleryImages = new File(DirectoryManager.getGameGalleryDirectory(gameOfThePage.getId())).listFiles();
        String[] images = new String[galleryImages.length];
        for(int i=0;i<galleryImages.length;i++){
            images[i] = "file://" + galleryImages[i].getPath();
        }

        int position;


        Intent i = new Intent(this,ActivityGallery.class);
        i.putExtra("images",images);
        i.putExtra("position",(int)v.getTag());
        startActivity(i);
    }

    public void enlargeCover(View v){
        String[] images = new String[1];

        images[0] = "file://" + gameOfThePage.getCover();

        Intent i = new Intent(this,ActivityGallery.class);
        i.putExtra("images",images);
        i.putExtra("position",0);
        startActivity(i);
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
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gameOfThePage.getURL()));
                    startActivity(intent);
                }else if(item.getTitle().toString().equals(getString(R.string.more_options_settings))){
                    Intent i = new Intent(getApplicationContext(), ActivitySettings.class);
                    startActivity(i);
                }
                return false;
            }
        });

        popup.show();
    }

    //Copy directory if game not exists
    public boolean copyDirectory(File sourceLocation , File targetLocation) throws IOException {
        if(targetLocation.isDirectory() && targetLocation.exists()){
            return false;
        }

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

        return true;
    }

    //Open the official site of the game
    public void goToGameOfficialSite(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gameOfThePage.getOfficialSite()));
        startActivity(intent);
    }

    //Open the official site of the game
    public void goToPromoMessageUrl(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + gameOfThePage.getPromo().get(0).getMessageURL()));
        startActivity(intent);
    }
}

