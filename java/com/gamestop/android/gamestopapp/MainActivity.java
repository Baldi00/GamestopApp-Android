package com.gamestop.android.gamestopapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout pullToRefresh;
    private LinearLayout newsPage, wishlistPage, searchPage;
    private EditText gameToSearch;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsPage = (LinearLayout)findViewById(R.id.newsPage);
        wishlistPage = (LinearLayout)findViewById(R.id.wishlistPage);
        searchPage = (LinearLayout)findViewById(R.id.searchPage);
        gameToSearch = (EditText)findViewById(R.id.gameToSearch);

        //Button navigation view, 3 pages switcher
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_news:
                        wishlistPage.setVisibility(View.GONE);
                        searchPage.setVisibility(View.GONE);
                        newsPage.setVisibility(View.VISIBLE);
                        if(findViewById(R.id.addGameToWishlist)!=null) findViewById(R.id.addGameToWishlist).setVisibility(View.GONE);
                        if(findViewById(R.id.deleteGameFromWishlist)!=null) findViewById(R.id.deleteGameFromWishlist).setVisibility(View.GONE);
                        return true;
                    case R.id.navigation_wishlist:
                        newsPage.setVisibility(View.GONE);
                        searchPage.setVisibility(View.GONE);
                        wishlistPage.setVisibility(View.VISIBLE);
                        if(findViewById(R.id.addGameToWishlist)!=null) findViewById(R.id.addGameToWishlist).setVisibility(View.GONE);
                        if(findViewById(R.id.deleteGameFromWishlist)!=null) findViewById(R.id.deleteGameFromWishlist).setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigation_search:
                        newsPage.setVisibility(View.GONE);
                        wishlistPage.setVisibility(View.GONE);
                        searchPage.setVisibility(View.VISIBLE);
                        if(findViewById(R.id.addGameToWishlist)!=null) findViewById(R.id.addGameToWishlist).setVisibility(View.VISIBLE);
                        if(findViewById(R.id.deleteGameFromWishlist)!=null) findViewById(R.id.deleteGameFromWishlist).setVisibility(View.GONE);
                        return true;
                }
                return false;
            }
        };

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Add
        Games wishlistData = new Games();
        wishlistData.add(new Game("Detroit: Become Human","PS4", "Quantic Dream",70.98, 40.98,"test1.jpg"));
        wishlistData.add(new Game("Horizon Zero Dawn","PS4","Guerrilla Games",70.98, 40.98, "test2.jpg"));
        wishlistData.add(new Game("GTA V","PC","Rockstar Games",50.98, 34.98,"test3.jpg"));

        //List with listener (wishlist & searchedGame lists)
        ListView wishistView = (ListView)findViewById(R.id.wishlistView);
        ListView searchedGameList = (ListView)findViewById(R.id.searchedGameList);

        wishistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Game g = (Game)parent.getItemAtPosition(position);

                Intent i = new Intent(view.getContext(),ActivityGamePage.class);
                i.putExtra("source","wishlist");
                i.putExtra("title",g.getTitle());
                i.putExtra("platform",g.getPlatform());
                i.putExtra("publisher",g.getPublisher());
                i.putExtra("cover",g.getCover());
                i.putExtra("newPrice",g.getNewPrice());
                i.putExtra("usedPrice",g.getUsedPrice());
                startActivity(i);
            }
        });

        wishistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Toast.makeText(arg1.getContext(),"pos: " + pos,Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        searchedGameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Game g = (Game)parent.getItemAtPosition(position);

                Intent i = new Intent(view.getContext(),ActivityGamePage.class);
                i.putExtra("source","searchGameList");
                i.putExtra("title",g.getTitle());
                i.putExtra("platform",g.getPlatform());
                i.putExtra("publisher",g.getPublisher());
                i.putExtra("cover",g.getCover());
                i.putExtra("newPrice",g.getNewPrice());
                i.putExtra("usedPrice",g.getUsedPrice());
                startActivity(i);
            }
        });

        //Adapters
        GameAdapter adapter = new GameAdapter(wishlistData,this);
        wishistView.setAdapter(adapter);
        searchedGameList.setAdapter(adapter);

        //Swipe down to refresh
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data

                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void startGameActivity(View v){
        Intent i = new Intent(this,ActivityGamePage.class);
        startActivity(i);
    }

    public void searchGame(View v){
        if(!gameToSearch.getText().toString().equals("")) {
            startSearch();
            gameToSearch.setEnabled(false);
        }else{
            Toast.makeText(this,"Gioco non inserito", Toast.LENGTH_SHORT).show();
        }
    }

    public void startSearch(){
        Search s = new Search(this);
        s.execute();
        findViewById(R.id.resultsOfSearchLayout).setVisibility(View.GONE);
        findViewById(R.id.progressBarOnSearch).setVisibility(View.VISIBLE);
    }

    public void onEndSearch(Object result){
        findViewById(R.id.progressBarOnSearch).setVisibility(View.GONE);
        ((TextView) (findViewById(R.id.resultsFound))).setText("3");
        ((TextView) (findViewById(R.id.searchedGame))).setText(((EditText)(findViewById(R.id.gameToSearch))).getText().toString());
        findViewById(R.id.resultsOfSearchLayout).setVisibility(View.VISIBLE);
        gameToSearch.setEnabled(true);
    }

    //MENU TOOLBAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_activity_game_page, menu);
        navigation.setSelectedItemId(R.id.navigation_wishlist);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addGameToWishlist:
                Toast.makeText(this,"Io aggiungo giochi", Toast.LENGTH_SHORT).show();
            case R.id.deleteGameFromWishlist:
                Toast.makeText(this,"Io cancello giochi", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
