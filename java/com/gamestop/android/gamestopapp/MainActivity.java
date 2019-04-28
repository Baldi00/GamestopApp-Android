package com.gamestop.android.gamestopapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

    //Bottom bar, 3 pages switch management
    private BottomNavigationView navigation;

    //Swipe layout, update management
    private SwipeRefreshLayout pullToRefresh;

    //Lists
    private static List<GamePreview> wishlistData;                  //Data
    private List<GamePreview> searchedGameListData;
    private ListView wishlistView, searchedGameListView; //View
    private static GameAdapter wishlistAdapter;         //Adapter
    private GameAdapter searchedGameListAdapter;

    //Searcher bar in the "Searcher" page
    private EditText gameToSearch;

    //OTHER ATTRIBUTES


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

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        wishlistView = (ListView)findViewById(R.id.wishlistView);
        searchedGameListView = (ListView)findViewById(R.id.searchedGameList);

        gameToSearch = (EditText)findViewById(R.id.gameToSearch);


        //LISTENERS

        navigation.setOnNavigationItemSelectedListener(new MyOnNavigationItemSelectedListener(newsPage,wishlistPage,searchPage,goToSearch,this));

        wishlistView.setOnItemClickListener(new MyOnItemClickListener(this,"wishlist"));
        searchedGameListView.setOnItemClickListener(new MyOnItemClickListener(this,"searchGameList"));

        pullToRefresh.setOnRefreshListener(new MyOnRefreshListener(pullToRefresh,this));


        //OPERATIONS

        wishlistData = new ArrayList();
        searchedGameListData = new ArrayList();

        //Adapters
        //Maybe 2 different adapter classes in the future

        wishlistAdapter = new GameAdapter(wishlistData,this);
        searchedGameListAdapter = new GameAdapter(searchedGameListData,this);

        wishlistView.setAdapter(wishlistAdapter);
        searchedGameListView.setAdapter(searchedGameListAdapter);

        wishlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext(), R.style.DialogActivityGamePage);
                dialog.setMessage("Vuoi aggiungere i giochi selezionati alla wishlist?");
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
                return false;
            }
        });

        searchedGameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                addToList(view,position);
                return true;
            }
        });

        //Add data, ************* TEST *************
        /*Downloader downloader1 = new Downloader(this,"https://www.gamestop.it/PS4/Games/110143/detroit-become-human");
        Downloader downloader2 = new Downloader(this,"https://www.gamestop.it/PS4/Games/97544/horizon-zero-dawn");
        Downloader downloader3 = new Downloader(this,"https://www.gamestop.it/PS4/Games/34052/gta-v");
        downloader1.execute();
        downloader2.execute();
        downloader3.execute();*/

        //TEST
        /*
        searchedGameListData.add(new Game("https://www.gamestop.it/PS4/Games/110143/detroit-become-human"));
        searchedGameListData.add(new Game("https://www.gamestop.it/PS4/Games/97544/horizon-zero-dawn"));
        searchedGameListData.add(new Game("https://www.gamestop.it/PC/Games/34054/gta-v"));*/


        //Set "Wishlist" page as first page
        navigation.setSelectedItemId(R.id.navigation_wishlist);
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

    //RESEARCH A GAME ON GAMESTOP WEBSITE

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

    }

    //Show the menu on more options button
    public void showMoreOptionsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.more_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().toString().equals(getString(R.string.more_options_sort))){
                    showSortByPopupMenu();
                }else if(item.getTitle().toString().equals(getString(R.string.more_options_open_site))){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gamestop.it/"));
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

    //Show the sorting menu on more options button
    public void showSortByPopupMenu() {
        PopupMenu popup = new PopupMenu(this, more);
        MenuInflater inflater = popup.getMenuInflater();

        if(navigation.getSelectedItemId() == R.id.navigation_wishlist) {
            inflater.inflate(R.menu.more_options_menu_sort_whishlist, popup.getMenu());
        } else if (navigation.getSelectedItemId() == R.id.navigation_search){
            inflater.inflate(R.menu.more_options_menu_sort_search, popup.getMenu());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(),"Non ancora implementato",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        popup.show();
    }

    //Add games into wishlist if caller is a game from search list
    public void addToList(View v, final int position){
        String titolo = ((TextView)v.findViewById(R.id.title)).getText().toString();
        //titolo = titolo.substring(0,titolo.length()-2);             //Remove strange space at the end
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("Vuoi aggiungere " + titolo + " alla wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wishlistData.add((GamePreview)searchedGameListView.getAdapter().getItem(position));
                        wishlistAdapter.notifyDataSetChanged();
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

    //Remove games from wishlist if caller is a game from wishlist
    public void removeFromList(View v){

    }

    //Set default background color to every view
    private void setDefaultBackgroundColorToAllListViews(@NonNull ListView list){
        for(int i=0;i<list.getAdapter().getCount();i++){
            list.getAdapter().getView(i,null, wishlistView).setBackgroundColor(Color.parseColor("#EEEEEE"));
        }

        ((ArrayAdapter)list.getAdapter()).notifyDataSetChanged();
    }

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

    public ListView getWishlistView() {
        return wishlistView;
    }

    public ListView getSearchedGameListView() {
        return searchedGameListView;
    }

    public static void addGameFromGamePage(GamePreview g){
        wishlistData.add(g);
        wishlistAdapter.notifyDataSetChanged();
    }

    public static void removeGameFromGamePage(Game g){
        wishlistData.remove(g);
        wishlistAdapter.notifyDataSetChanged();
    }

    public void onEndDownload(Object g){
        if(g!=null) {
            wishlistData.add((GamePreview) g);
            wishlistAdapter.notifyDataSetChanged();
        }
    }

    public void goToSearch(View v){
        navigation.setSelectedItemId(R.id.navigation_search);
    }
}
