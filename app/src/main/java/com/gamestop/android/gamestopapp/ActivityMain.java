package com.gamestop.android.gamestopapp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity{

    /*********************************************************************************************************************************/


    /*****************************************/
    /************   ATTRIBUTES   *************/
    /*****************************************/

    //GRAPHICS VIEWS

    //Toolbar buttons
    private ImageButton goToSearch,more;

    //The 3 pages
    private LinearLayout newsPage, wishlistPage, searchPage;

    //Gamestop bunny
    private static LinearLayout bunnyWishlist, bunnySearch;
    private static ImageView bunnyWishlistImage, bunnySearchImage;

    //Bottom bar, 3 pages switch management
    private static BottomNavigationView navigation;

    //Swipe layout, update management
    private SwipeRefreshLayout pullToRefreshLayout;
    private MyOnRefreshListener pullToRefreshListener;


    //Lists
    private static Games wishlistData;                  //Data
    private static Games searchedGameListData;
    private static ListView wishlistView, searchedGameListView; //View
    private static GameAdapter wishlistAdapter;         //Adapter
    private static GameAdapter searchedGameListAdapter;

    //Searcher bar in the "Searcher" page
    private EditText gameToSearch;

    //System
    private static Context appContext;
    private static boolean active = false;

    // CacheManager
    private static CacheManager cache;

    //SettingsManager
    private static SettingsManager settingsManager;


    /*********************************************************************************************************************************/


    /*****************************************/
    /************   CALLBACKS   **************/
    /*****************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //GET ALL VIEWS FROM APP GRAPHIC

        goToSearch = (ImageButton) findViewById(R.id.goToSearch);
        more = (ImageButton) findViewById(R.id.more);

        newsPage = (LinearLayout) findViewById(R.id.newsPage);
        wishlistPage = (LinearLayout) findViewById(R.id.wishlistPage);
        searchPage = (LinearLayout) findViewById(R.id.searchPage);

        bunnyWishlist = (LinearLayout) findViewById(R.id.gamestop_bunny_wishlist);
        bunnySearch = (LinearLayout) findViewById(R.id.gamestop_bunny_search);
        bunnyWishlistImage = (ImageView) findViewById(R.id.gamestop_bunny_wishlist_image);
        bunnySearchImage = (ImageView) findViewById(R.id.gamestop_bunny_search_image);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        pullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        wishlistView = (ListView) findViewById(R.id.wishlistView);
        searchedGameListView = (ListView) findViewById(R.id.searchedGameList);

        gameToSearch = (EditText) findViewById(R.id.gameToSearch);


        //LISTENERS

        navigation.setOnNavigationItemSelectedListener(new MyOnNavigationItemSelectedListener(newsPage, wishlistPage, searchPage, goToSearch, this));

        wishlistView.setOnItemClickListener(new MyOnItemClickListener(this, "wishlist"));
        searchedGameListView.setOnItemClickListener(new MyOnItemClickListener(this, "search"));

        pullToRefreshListener = new MyOnRefreshListener(pullToRefreshLayout, this);
        pullToRefreshLayout.setOnRefreshListener(pullToRefreshListener);

        //Search icon on keyboard
        gameToSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchGame(null);
                    return true;
                }
                return false;
            }
        });

        wishlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(view, position);
                return true;
            }
        });

        searchedGameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                addToWishlistSearchPage(view, position);
                return true;
            }
        });


        //OPERATIONS

        wishlistData = new Games();
        searchedGameListData = new Games();

        //Adapters
        //Maybe 2 different adapter classes in the future

        wishlistAdapter = new GameAdapter(wishlistData, this);
        searchedGameListAdapter = new GameAdapter(searchedGameListData, this);

        wishlistView.setAdapter(wishlistAdapter);
        searchedGameListView.setAdapter(searchedGameListAdapter);

        //Set "Wishlist" page as first page
        navigation.setSelectedItemId(R.id.navigation_wishlist);


        //Check and do update at startup
        try {
            settingsManager = SettingsManager.getInstance();
            boolean updateOnStartEnabled = settingsManager.isUpdateOnStartEnabled();
            if(updateOnStartEnabled) {
                pullToRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshLayout.setRefreshing(true);
                        pullToRefreshListener.onRefresh();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create validator file (xsd) if not exists
        try {
            DirectoryManager.createValidatorFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Start background service if not already running
        if(!isBackgroundServiceRunning(BackgroundService.class)) {
            Intent service = new Intent(this, BackgroundService.class);
            startService(service);
        }

        //Create config file with notification id
        File configNotificationId = new File(DirectoryManager.getAppDir() + "notificationId.txt");
        if (!configNotificationId.exists()) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(configNotificationId));
                bw.write("0"); //Notification id
                bw.newLine();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Set application static context
        appContext = getApplicationContext();


        // CACHE
        cache = CacheManager.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active=true;
        try {
            Games temp = DirectoryManager.importGames();
            for(GamePreview gp : temp){
                wishlistData.add(gp);
                cache.addBitmapToMemCache(gp.getCover());
            }
            wishlistAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
        checkAndSetGamestopBunny();
    }

    @Override
    protected void onResume() {
        super.onResume();
        active=true;
        checkAndSetGamestopBunny();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        active=true;
        checkAndSetGamestopBunny();
    }

    @Override
    protected void onPause() {
        super.onPause();
        active=false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            DirectoryManager.exportGames(wishlistData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        active=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            DirectoryManager.deleteTempGames(wishlistData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        //Check if it is on the first page
        if(navigation.getSelectedItemId() != R.id.navigation_wishlist){
            navigation.setSelectedItemId(R.id.navigation_wishlist);
            return;
        }

        super.onBackPressed();

    }

    /*********************************************************************************************************************************/


    /*****************************************/
    /**********   OTHER METHODS   ************/
    /*****************************************/

    //RESEARCH GAME ON GAMESTOP WEBSITE

    //Checks if game is set and if connection is set
    public void searchGame(View v){
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if(isConnected){
            if(!gameToSearch.getText().toString().equals("")) {
                searchedGameListData.clear();
                searchedGameListAdapter.notifyDataSetChanged();
                startSearch();
            }else{
                Toast.makeText(this,"Gioco non inserito", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Non sei connesso a internet", Toast.LENGTH_SHORT).show();
        }

    }

    //Start the thread that will do the research. Then enable/disable elements
    private void startSearch(){
        Searcher s = new Searcher(this,gameToSearch.getText().toString());
        s.execute();
        gameToSearch.setEnabled(false);
        findViewById(R.id.resultsOfSearchLayout).setVisibility(View.GONE);
        findViewById(R.id.progressBarOnSearch).setVisibility(View.VISIBLE);
        bunnySearch.setVisibility(View.GONE);
    }

    //Receive research results and enable TextView
    public void onEndSearch(Object result){
        if(result!=null){
            for(GamePreview g : (ArrayList<GamePreview>)result){
                searchedGameListData.add(g);
                cache.addBitmapToMemCache(g.getCover());
            }
            searchedGameListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this,"Nessun risultato trovato", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.progressBarOnSearch).setVisibility(View.GONE);
        findViewById(R.id.resultsOfSearchLayout).setVisibility(View.VISIBLE);
        gameToSearch.setEnabled(true);
        searchedGameListView.smoothScrollToPosition(0);
        checkAndSetGamestopBunny();
    }



    //ADD AND REMOVE SYSTEM

    //Add a game into wishlist if caller is a game from search list
    public void addToWishlistSearchPage(View v, final int position){
        String titolo = ((TextView)v.findViewById(R.id.title)).getText().toString();
        //titolo = titolo.substring(0,titolo.length()-2);             //Remove strange space at the end
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("Vuoi aggiungere " + titolo + " alla wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(),ActivityGamePage.class);
                        i.putExtra("url",((GamePreview)searchedGameListAdapter.getItem(position)).getURL());
                        i.putExtra("source","toAdd");
                        startActivity(i);
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

    //Remove a game from wishlist if caller is a game from wishlist
    public void removeFromWishlist(View v, final int position){
        String titolo = ((TextView)v.findViewById(R.id.title)).getText().toString();
        titolo = titolo.substring(0,titolo.length()-2);             //Remove strange space at the end
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("Vuoi rimuovere " + titolo + " dalla wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wishlistData.remove((GamePreview)wishlistAdapter.getItem(position));
                        wishlistAdapter.notifyDataSetChanged();

                        try {
                            DirectoryManager.exportGames(wishlistData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        checkAndSetGamestopBunny();
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

    //Add a game into wishlist if caller is a game from ActivityGamePage
    public static void addToWishlistGamePage(Game g){

        try {
            DirectoryManager.exportGame(g);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean result = wishlistData.add(g);
        wishlistAdapter.notifyDataSetChanged();

        if(result) {
            Toast.makeText(appContext, g.getTitle().substring(0,g.getTitle().length()-2) + " è stato aggiunto alla wishlist", Toast.LENGTH_SHORT).show();

            try {
                DirectoryManager.exportGames(wishlistData);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Go to wishlist at the last position
            navigation.setSelectedItemId(R.id.navigation_wishlist);
            wishlistView.smoothScrollToPosition(wishlistAdapter.getCount()-1);

        } else {
            Toast.makeText(appContext, g.getTitle().substring(0,g.getTitle().length()-2) + " è già presente nella wishlist", Toast.LENGTH_SHORT).show();
        }

        checkAndSetGamestopBunny();
    }

    //Remove a game from wishlist if caller is a game from ActivityGamePage
    public static void removeFromWishlistGamePage(Game g){
        wishlistData.remove(g);
        wishlistAdapter.notifyDataSetChanged();
        try {
            DirectoryManager.exportGames(wishlistData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkAndSetGamestopBunny();
    }

    //Update wishlist
    public static void updateWishlist(){
        try {
            Games temp = DirectoryManager.importGames();

            //If coming from backservice with inactive app
            if(!active) {
                wishlistData = new Games();
            }

            wishlistData.clear();
            for (GamePreview gp : temp) {
                wishlistData.add(gp);
            }

            if(active){
                wishlistAdapter.notifyDataSetChanged();
            }

            DirectoryManager.exportGames(wishlistData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //ACTION BAR BUTTONS

    //Called when user press on add button in wishlist page
    public void goToSearch(View v){
        navigation.setSelectedItemId(R.id.navigation_search);
    }

    //Show the menu on more options button
    public void showMoreOptionsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();

        if(navigation.getSelectedItemId() == R.id.navigation_wishlist) {
            inflater.inflate(R.menu.more_options_menu_wishlist, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle().toString().equals(getString(R.string.more_options_sort))) {
                        showSortByPopupMenu();
                    } else if (item.getTitle().toString().equals(getString(R.string.more_options_open_site))) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gamestop.it/"));
                        startActivity(intent);
                    } else if (item.getTitle().toString().equals(getString(R.string.more_options_settings))) {
                        Intent i = new Intent(getApplicationContext(), ActivitySettings.class);
                        startActivity(i);
                    }
                    return false;
                }
            });
        } else if(navigation.getSelectedItemId() == R.id.navigation_search){
            inflater.inflate(R.menu.more_options_menu_search, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle().toString().equals(getString(R.string.more_options_open_on_site))) {
                        Intent intent = null;
                        try {
                            if(!gameToSearch.getText().toString().equals("")) {
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gamestop.it/SearchResult/QuickSearch?q=" + URLEncoder.encode(gameToSearch.getText().toString(), "UTF-8")));
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Gioco non inserito", Toast.LENGTH_SHORT).show();
                            }
                        } catch (UnsupportedEncodingException e) {
                            Toast.makeText(getApplicationContext(),"Impossibile aprire su gamestop",Toast.LENGTH_SHORT).show();
                        }
                    } else if (item.getTitle().toString().equals(getString(R.string.more_options_settings))) {
                        Intent i = new Intent(getApplicationContext(), ActivitySettings.class);
                        startActivity(i);
                    }
                    return false;
                }
            });
        } else {
            inflater.inflate(R.menu.more_options_menu_promo, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle().toString().equals(getString(R.string.more_options_settings))) {
                        Intent i = new Intent(getApplicationContext(), ActivitySettings.class);
                        startActivity(i);
                    }
                    return false;
                }
            });
        }

        popup.show();
    }

    //Show the sorting menu on more options button
    public void showSortByPopupMenu() {
        PopupMenu popup = new PopupMenu(this, more);
        MenuInflater inflater = popup.getMenuInflater();

        if(navigation.getSelectedItemId() == R.id.navigation_wishlist) {
            inflater.inflate(R.menu.more_options_menu_sort_whishlist, popup.getMenu());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().toString().equals(getString(R.string.sort_by_aplhabet))) {
                    wishlistData.sortbyName();
                } else if (item.getTitle().toString().equals(getString(R.string.sort_by_platform))) {
                    wishlistData.sortByPlatform();
                } else if (item.getTitle().toString().equals(getString(R.string.sort_by_date))) {
                    wishlistData.sortByReleaseDate();
                } else if (item.getTitle().toString().equals(getString(R.string.sort_by_new_price))) {
                    wishlistData.sortByNewPrice();
                } else if (item.getTitle().toString().equals(getString(R.string.sort_by_used_price))) {
                    wishlistData.sortByUsedPrice();
                }

                wishlistAdapter.notifyDataSetChanged();

                try {
                    DirectoryManager.exportGames(wishlistData);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                wishlistView.smoothScrollToPosition(0);

                return false;
            }
        });

        popup.show();
    }



    // CUSTOM SORT & REMOVE DIALOG

    //Show dialog on a game in wishlist
    public void showDialog(final View view, final int position){
        String titolo = ((TextView)view.findViewById(R.id.title)).getText().toString();
        titolo = titolo.substring(0,titolo.length()-2);             //Remove strange space at the end

        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.activity_main_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(dialogView);

        ((TextView)dialogView.findViewById(R.id.titolo)).setText(titolo);

        dialogView.findViewById(R.id.moveUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                customSort(0, position);
            }
        });

        dialogView.findViewById(R.id.moveDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customSort(1, position);
                dialog.cancel();
            }
        });

        dialogView.findViewById(R.id.moveTop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customSort(2, position);
                dialog.cancel();
            }
        });

        dialogView.findViewById(R.id.moveBottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customSort(3, position);
                dialog.cancel();
            }
        });

        dialogView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                removeFromWishlist(view,position);
            }
        });

        dialogView.findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    //Move by steps the items in wishlist
    public void customSort (int action, int position){
        GamePreview g = wishlistData.get(position);
        switch (action){
            case 0:
                if(position>0) {
                    wishlistData.add(position-1,g);
                    wishlistData.remove(position+1);
                    wishlistAdapter.notifyDataSetChanged();
                }
                break;
            case 1:
                if(position<wishlistData.size()-1)
                    customSort(0,position+1);
                break;
            case 2:
                wishlistData.add(0,g);
                wishlistData.remove(position+1);
                wishlistAdapter.notifyDataSetChanged();
                break;
            case 3:
                wishlistData.add(wishlistData.size(),g);
                wishlistData.remove(position);
                wishlistAdapter.notifyDataSetChanged();
                break;
        }
    }



    //PROMO PAGE

    public void promoPlaystation(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gamestop.it/playstation?utm_source=Pulsante&utm_medium=Homepage&utm_campaign=PlayStationPromo"));
        startActivity(intent);
    }

    public void promoNintendo(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gamestop.it/nintendo?utm_source=Pulsante&utm_medium=Homepage&utm_campaign=nintendo#intro"));
        startActivity(intent);
    }

    public void promoXbox(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gamestop.it/xbox?utm_source=Pulsante&utm_medium=Homepage&utm_campaign=xbox"));
        startActivity(intent);
    }



    //GETTERS AND SETTERS

    public ListView getWishlistView() {
        return wishlistView;
    }

    public static Games getWishlistData() {
        return wishlistData;
    }

    public static GameAdapter getWishlistAdapter() {
        return wishlistAdapter;
    }



    //OTHER METHODS

    public static void resetResearh(){
        searchedGameListData.clear();
        searchedGameListAdapter.notifyDataSetChanged();
        checkAndSetGamestopBunny();
    }

    public static void resetAll(){
        resetResearh();
        wishlistData.clear();
        wishlistAdapter.notifyDataSetChanged();
        checkAndSetGamestopBunny();
    }

    public static void checkAndSetGamestopBunny(){
        boolean bunnyEnabled = settingsManager.isBunnyEnabled();

        if(bunnyEnabled){
            bunnySearchImage.setVisibility(View.VISIBLE);
            bunnyWishlistImage.setVisibility(View.VISIBLE);
        }else{
            bunnySearchImage.setVisibility(View.GONE);
            bunnyWishlistImage.setVisibility(View.GONE);
        }

        checkAndSetGamestopBunnyWishlist();
        checkAndSetGamestopBunnySearch();
    }

    public static void checkAndSetGamestopBunnyWishlist(){
        if(wishlistData.size()==0){
            bunnyWishlist.setVisibility(View.VISIBLE);
        }else{
            bunnyWishlist.setVisibility(View.GONE);
        }
    }

    public static void checkAndSetGamestopBunnySearch(){
        if(searchedGameListData.size()==0){
            bunnySearch.setVisibility(View.VISIBLE);
        }else{
            bunnySearch.setVisibility(View.GONE);
        }
    }

    private boolean isBackgroundServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
