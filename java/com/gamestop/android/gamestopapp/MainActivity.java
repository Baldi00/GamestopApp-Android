package com.gamestop.android.gamestopapp;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    /*********************************************************************************************************************************/


    /*****************************************/
    /************   ATTRIBUTES   *************/
    /*****************************************/

    //Toolbar buttons
    private ImageButton add,remove,more;

    //The 3 pages
    private LinearLayout newsPage, wishlistPage, searchPage;

    //Bottom bar, 3 pages switch management
    private BottomNavigationView navigation;

    //Swipe layout, update management
    private SwipeRefreshLayout pullToRefresh;

    //Lists
    private Games wishlistData, searchedGameListData;               //Data
    private ListView wishistView, searchedGameListView;             //View
    private GameAdapter wishlistAdapter, searchedGameListAdapter;   //Adapter

    //Others
    private EditText gameToSearch; //Searcher bar in the "Searcher" page


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
        more = (ImageButton)findViewById(R.id.more);

        newsPage = (LinearLayout)findViewById(R.id.newsPage);
        wishlistPage = (LinearLayout)findViewById(R.id.wishlistPage);
        searchPage = (LinearLayout)findViewById(R.id.searchPage);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        wishistView = (ListView)findViewById(R.id.wishlistView);
        searchedGameListView = (ListView)findViewById(R.id.searchedGameList);

        gameToSearch = (EditText)findViewById(R.id.gameToSearch);


        //LISTENERS

        navigation.setOnNavigationItemSelectedListener(new MyOnNavigationItemSelectedListener(newsPage,wishlistPage,searchPage,add,remove));

        wishistView.setOnItemClickListener(new MyOnItemClickListener(this));
        searchedGameListView.setOnItemClickListener(new MyOnItemClickListener(this));

        pullToRefresh.setOnRefreshListener(new MyOnRefreshListener(pullToRefresh));


        //OPERATIONS

        //Set "Wishlist" page as first page
        navigation.setSelectedItemId(R.id.navigation_wishlist);

        //Add data, ************* TEST *************
        wishlistData = new Games();
        wishlistData.add(new Game("Detroit: Become Human","PS4", "Quantic Dream",70.98, 40.98,"test1.jpg"));
        wishlistData.add(new Game("Horizon Zero Dawn","PS4","Guerrilla Games",70.98, 40.98, "test2.jpg"));
        wishlistData.add(new Game("GTA V","PC","Rockstar Games",50.98, 34.98,"test3.jpg"));


        //Adapters
        //Maybe 2 different adapter classes in the future

        wishlistAdapter = new GameAdapter(wishlistData,this);
        //searchedGameListAdapter = new GameAdapter(searchedGameListData,this);

        wishistView.setAdapter(wishlistAdapter);
        //searchedGameListView.setAdapter(searchedGameListAdapter);

        //TEMP, ONLY FOR TEST
        searchedGameListView.setAdapter(wishlistAdapter);

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
        ((TextView) (findViewById(R.id.resultsFound))).setText("3");
        findViewById(R.id.resultsOfSearchLayout).setVisibility(View.VISIBLE);
        gameToSearch.setEnabled(true);

    }

}
