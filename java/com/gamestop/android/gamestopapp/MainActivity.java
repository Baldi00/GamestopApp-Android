package com.gamestop.android.gamestopapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

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

    //Bottom bar, 3 pages switch management
    private BottomNavigationView navigation;

    //Swipe layout, update management
    private SwipeRefreshLayout pullToRefresh;

    //Lists
    private static Games wishlistData;                  //Data
    private static Games searchedGameListData;
    private ListView wishlistView, searchedGameListView; //View
    private static GameAdapter wishlistAdapter;         //Adapter
    private static GameAdapter searchedGameListAdapter;

    //Searcher bar in the "Searcher" page
    private EditText gameToSearch;


    /*********************************************************************************************************************************/


    /*****************************************/
    /************   CALLBACKS   **************/
    /*****************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //GET ALL VIEWS FROM APP GRAPHIC

        goToSearch = (ImageButton)findViewById(R.id.goToSearch);
        more = (ImageButton) findViewById(R.id.more);

        newsPage = (LinearLayout)findViewById(R.id.newsPage);
        wishlistPage = (LinearLayout)findViewById(R.id.wishlistPage);
        searchPage = (LinearLayout)findViewById(R.id.searchPage);

        bunnyWishlist = (LinearLayout)findViewById(R.id.gamestop_bunny_wishlist);
        bunnySearch = (LinearLayout)findViewById(R.id.gamestop_bunny_search);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        wishlistView = (ListView)findViewById(R.id.wishlistView);
        searchedGameListView = (ListView)findViewById(R.id.searchedGameList);

        gameToSearch = (EditText)findViewById(R.id.gameToSearch);



        //LISTENERS

        navigation.setOnNavigationItemSelectedListener(new MyOnNavigationItemSelectedListener(newsPage,wishlistPage,searchPage,goToSearch,this));

        wishlistView.setOnItemClickListener(new MyOnItemClickListener(this,"wishlist"));
        searchedGameListView.setOnItemClickListener(new MyOnItemClickListener(this,"search"));

        pullToRefresh.setOnRefreshListener(new MyOnRefreshListener(pullToRefresh,this));

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

        //OPERATIONS

        wishlistData = new Games();
        searchedGameListData = new Games();

        //Adapters
        //Maybe 2 different adapter classes in the future

        wishlistAdapter = new GameAdapter(wishlistData,this);
        searchedGameListAdapter = new GameAdapter(searchedGameListData,this);

        wishlistView.setAdapter(wishlistAdapter);
        searchedGameListView.setAdapter(searchedGameListAdapter);

        wishlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeFromWishlist(view,position);
                return true;
            }
        });

        searchedGameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                addToWishlistSearchPage(view,position);
                return true;
            }
        });

        //Set "Wishlist" page as first page
        navigation.setSelectedItemId(R.id.navigation_wishlist);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Games temp = Games.importBinary(this);
            for(GamePreview gp : temp){
                wishlistData.add(gp);
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e){
            Toast.makeText(this,"Errore durante importazione dei giochi", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        wishlistAdapter.notifyDataSetChanged();
        checkAndSetGamestopBunnyWishlist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndSetGamestopBunnyWishlist();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkAndSetGamestopBunnyWishlist();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            wishlistData.exportBinary(this);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            Toast.makeText(this,"Errore durante il salvataggio dei giochi",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File f = new File(DirectoryManager.getTempDir());
        deleteFolderRecursive(f);
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

    //RESEARCH AND DOWNLOAD A GAME ON GAMESTOP WEBSITE

    //Checks if game is set
    public void searchGame(View v){

        if(!gameToSearch.getText().toString().equals("")) {
            searchedGameListData.clear();
            searchedGameListAdapter.notifyDataSetChanged();
            startSearch();
        }else{
            Toast.makeText(this,"Gioco non inserito", Toast.LENGTH_SHORT).show();
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
            }
            searchedGameListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this,"Nessun risultato trovato", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.progressBarOnSearch).setVisibility(View.GONE);
        findViewById(R.id.resultsOfSearchLayout).setVisibility(View.VISIBLE);
        gameToSearch.setEnabled(true);
        searchedGameListView.smoothScrollToPosition(0);
        checkAndSetGamestopBunnySearch();
    }

    //Called when download of game finishes. Add downloaded game into wishlist
    public void onEndDownload(Object g){
        if(g!=null) {
            wishlistData.add((GamePreview) g);
            wishlistAdapter.notifyDataSetChanged();
        }
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
                        deleteFolderRecursive(new File(DirectoryManager.getWishlistDir() + ((GamePreview)wishlistAdapter.getItem(position)).getId() + "/"));
                        wishlistData.remove((GamePreview)wishlistAdapter.getItem(position));
                        wishlistAdapter.notifyDataSetChanged();
                        checkAndSetGamestopBunnyWishlist();
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
            g.exportBinary();
        } catch (IOException e) {
            e.printStackTrace();
        }
        wishlistData.add(g);
        wishlistAdapter.notifyDataSetChanged();
        checkAndSetGamestopBunnyWishlist();
    }

    //Remove a game from wishlist if caller is a game from ActivityGamePage
    public static void removeFromWishlistGamePage(Game g, Context main){
        wishlistData.remove(g);
        wishlistAdapter.notifyDataSetChanged();
        deleteFolderRecursive(new File(DirectoryManager.getWishlistDir() + g.getId() + "/"));
        checkAndSetGamestopBunnyWishlist();
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
                    if (item.getTitle().toString().equals(getString(R.string.more_options_add_id))) {
                        Toast.makeText(getApplicationContext(),"Non ancora implementato",Toast.LENGTH_SHORT).show();
                    } else if (item.getTitle().toString().equals(getString(R.string.more_options_open_on_site))) {
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
                } else if (item.getTitle().toString().equals(getString(R.string.sort_custom))) {
                    Toast.makeText(getApplicationContext(),"Non ancora implementato",Toast.LENGTH_SHORT).show();
                }
                wishlistAdapter.notifyDataSetChanged();
                return false;
            }
        });

        popup.show();
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

    //Delete a folder with its content
    public static void deleteFolderRecursive(File f){
        if(f.isDirectory()){
            File[] files = f.listFiles();
            for (File file : files){
                deleteFolderRecursive(file);
            }
        }

        f.delete();
    }

    public static void resetAll(){
        wishlistData.clear();
        wishlistAdapter.notifyDataSetChanged();
        resetSearch();
    }

    public static void resetSearch(){
        searchedGameListData.clear();
        searchedGameListAdapter.notifyDataSetChanged();
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
}
