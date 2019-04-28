package com.gamestop.android.gamestopapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    /*********************************************************************************************************************************/


    /*****************************************/
    /************   ATTRIBUTES   *************/
    /*****************************************/

    //GRAPHICS VIEWS

    //Toolbar buttons
    private ImageButton add,remove,more;

    //The 3 pages
    private LinearLayout newsPage, wishlistPage, searchPage;

    //Bottom bar, 3 pages switch management
    private BottomNavigationView navigation;

    //Swipe layout, update management
    private SwipeRefreshLayout pullToRefresh;

    //Lists
    private static Games wishlistData;                  //Data
    private Games searchedGameListData;
    private ListView wishistView, searchedGameListView; //View
    private static GameAdapter wishlistAdapter;         //Adapter
    private GameAdapter searchedGameListAdapter;

    //Searcher bar in the "Searcher" page
    private EditText gameToSearch;

    //OTHER ATTRIBUTES

    private boolean selecting;
    private boolean sorting;


    /*********************************************************************************************************************************/


    /*****************************************/
    /************   CALLBACKS   **************/
    /*****************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //GET ALL VIEWS FROM APP GRAPHIC

        add = (ImageButton)findViewById(R.id.add);
        remove = (ImageButton)findViewById(R.id.remove);
        more = (ImageButton) findViewById(R.id.more);

        newsPage = (LinearLayout)findViewById(R.id.newsPage);
        wishlistPage = (LinearLayout)findViewById(R.id.wishlistPage);
        searchPage = (LinearLayout)findViewById(R.id.searchPage);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        wishistView = (ListView)findViewById(R.id.wishlistView);
        searchedGameListView = (ListView)findViewById(R.id.searchedGameList);

        gameToSearch = (EditText)findViewById(R.id.gameToSearch);


        //LISTENERS

        navigation.setOnNavigationItemSelectedListener(new MyOnNavigationItemSelectedListener(newsPage,wishlistPage,searchPage,add,remove,this));

        wishistView.setOnItemClickListener(new MyOnItemClickListener(this,"wishlist"));
        searchedGameListView.setOnItemClickListener(new MyOnItemClickListener(this,"searchGameList"));

        pullToRefresh.setOnRefreshListener(new MyOnRefreshListener(pullToRefresh,this));


        //OPERATIONS

        //Add data, ************* TEST *************
        wishlistData = new Games();
        wishlistData.add(new Game("Detroit: Become Human","PS4", "Quantic Dream",70.98, 40.98,"test1.jpg"));
        wishlistData.add(new Game("Horizon Zero Dawn","PS4","Guerrilla Games",70.98, 40.98, "test2.jpg"));
        wishlistData.add(new Game("GTA V","PC","Rockstar Games",50.98, 34.98,"test3.jpg"));

        //TEST
        searchedGameListData = new Games();
        searchedGameListData.add(new Game("Detroit: Become Human","PS4", "Quantic Dream",70.98, 40.98,"test1.jpg"));
        searchedGameListData.add(new Game("Horizon Zero Dawn","PS4","Guerrilla Games",70.98, 40.98, "test2.jpg"));
        searchedGameListData.add(new Game("GTA V","PC","Rockstar Games",50.98, 34.98,"test3.jpg"));


        //Adapters
        //Maybe 2 different adapter classes in the future

        wishlistAdapter = new GameAdapter(wishlistData,this);
        searchedGameListAdapter = new GameAdapter(searchedGameListData,this);

        wishistView.setAdapter(wishlistAdapter);
        searchedGameListView.setAdapter(searchedGameListAdapter);

        wishistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selecting = true;
                view.setBackgroundColor(Color.parseColor("#FBD1D0"));   //Set temp color to let user see something has changed
                return false;
            }
        });

        searchedGameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selecting = true;
                view.setBackgroundColor(Color.parseColor("#FBD1D0"));   //Set temp color to let user see something has changed
                return false;
            }
        });


        //Set "Wishlist" page as first page
        navigation.setSelectedItemId(R.id.navigation_wishlist);
    }

    @Override
    public void onBackPressed() {

        //Check if it is selecting
        if(isSelecting()){
            selecting = false;

            //Set default background color to every view
            setDefaultBackgroundColorToAllListViews(wishistView);

            return;
        }

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

    //Checks if game is set and disable TextView, then starts the research
    public void searchGame(View v){

        if(!gameToSearch.getText().toString().equals("")) {
            startSearch();
            gameToSearch.setEnabled(false);
        }else{
            Toast.makeText(this,"Gioco non inserito", Toast.LENGTH_SHORT).show();
        }

    }

    //Start the thread that will do the research. NOT IMPLEMENTED YET
    private void startSearch(){

        Searcher s = new Searcher(this);
        s.execute();
        findViewById(R.id.resultsOfSearchLayout).setVisibility(View.GONE);
        findViewById(R.id.progressBarOnSearch).setVisibility(View.VISIBLE);

    }

    //Receive research results and enable TextView
    public void onEndSearch(Object result){

        findViewById(R.id.progressBarOnSearch).setVisibility(View.GONE);
        //((TextView) (findViewById(R.id.resultsFound))).setText("3");
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
    public void addToList(View v){
        if(navigation.getSelectedItemId() == R.id.navigation_wishlist){
            navigation.setSelectedItemId(R.id.navigation_search);
            return;
        }

        if(findViewById(R.id.resultsOfSearchLayout).getVisibility() == View.GONE){
            Toast.makeText(this,"Prima cerca un gioco",Toast.LENGTH_SHORT).show();
            return;
        }

        if(selecting) {

            final ArrayList<Integer> toAdd = new ArrayList();
            for(int i=0;i<searchedGameListData.size();i++){
                if(((Game)searchedGameListView.getItemAtPosition(i)).isSelected()){
                    toAdd.add(i);
                }
            }

            if(toAdd.size()==0) {
                Toast.makeText(getApplicationContext(), "Non hai selezionato nessun gioco da aggiungere", Toast.LENGTH_SHORT).show();
                setSelecting(false,searchedGameListView);
                return;
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
            dialog.setMessage("Vuoi aggiungere i giochi selezionati alla wishlist?");
            dialog.setPositiveButton(
                    "Si",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(int i=0;i<toAdd.size();i++){
                                wishlistData.add(searchedGameListData.get(Integer.parseInt(toAdd.get(i).toString())));
                            }

                            wishlistAdapter.notifyDataSetChanged();

                            Toast.makeText(getApplicationContext(),"I giochi selezionati sono stati aggiunti alla wishlist",Toast.LENGTH_SHORT).show();

                            setSelecting(false,searchedGameListView);
                            dialog.cancel();
                        }
                    });

            dialog.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setSelecting(false,searchedGameListView);
                            dialog.cancel();
                        }
                    });
            dialog.show();
        }else{
            setSelecting(true,searchedGameListView);
            Toast.makeText(this,"Seleziona i giochi da aggiungere",Toast.LENGTH_SHORT).show();
        }
    }

    //Remove games from wishlist if caller is a game from wishlist
    public void removeFromList(View v){
        if(selecting) {

            final ArrayList<Integer> toRemove = new ArrayList();
            for(int i=0;i<wishlistData.size();i++){
                if(((Game)wishistView.getItemAtPosition(i)).isSelected()){
                    toRemove.add(i);
                }
            }

            if(toRemove.size()==0) {
                Toast.makeText(getApplicationContext(), "Non hai selezionato nessun gioco da rimuovere", Toast.LENGTH_SHORT).show();
                setSelecting(false,wishistView);
                return;
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
            dialog.setMessage("Vuoi rimuovere i giochi selezionati dalla wishlist?");
            dialog.setPositiveButton(
                    "Si",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            for(int i=toRemove.size()-1;i>=0;i--){
                                wishlistData.remove(Integer.parseInt(toRemove.get(i).toString()));
                            }

                            wishlistAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),"I giochi selezionati sono stati rimossi dalla wishlist",Toast.LENGTH_SHORT).show();

                            setSelecting(false,wishistView);
                            dialog.cancel();
                        }
                    });

            dialog.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setSelecting(false,wishistView);
                            dialog.cancel();
                        }
                    });
            dialog.show();
        } else {
            setSelecting(true,wishistView);
            Toast.makeText(this,"Seleziona i giochi da rimuovere",Toast.LENGTH_SHORT).show();
        }
    }

    //Set default background color to every view
    private void setDefaultBackgroundColorToAllListViews(@NonNull ListView list){
        for(int i=0;i<list.getAdapter().getCount();i++){
            list.getAdapter().getView(i,null,wishistView).setBackgroundColor(Color.parseColor("#EEEEEE"));
        }

        ((ArrayAdapter)list.getAdapter()).notifyDataSetChanged();
    }

    public void setSelecting(boolean selecting, ListView list) {
        this.selecting = selecting;
        if(!selecting){
            setDefaultBackgroundColorToAllListViews(list);

            if(wishlistData!=null) {
                for (Game g : wishlistData) {
                    g.setSelected(false);
                }
            }

            if(searchedGameListData!=null) {
                for (Game g : searchedGameListData) {
                    g.setSelected(false);
                }
            }
        }
    }

    public boolean isSelecting() {
        return selecting;
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

    public ListView getWishistView() {
        return wishistView;
    }

    public ListView getSearchedGameListView() {
        return searchedGameListView;
    }

    public static void addGameFromGamePage(Game g){
        wishlistData.add(g);
        wishlistAdapter.notifyDataSetChanged();
    }

    public static void removeGameFromGamePage(Game g){
        wishlistData.remove(g);
        wishlistAdapter.notifyDataSetChanged();
    }
}
