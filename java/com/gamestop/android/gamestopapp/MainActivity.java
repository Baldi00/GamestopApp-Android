package com.gamestop.android.gamestopapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private ImageButton add,remove,more;

    private SwipeRefreshLayout pullToRefresh;
    private LinearLayout newsPage, wishlistPage, searchPage;
    private EditText gameToSearch;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    private Games wishlistData;
    private GameAdapter adapter;
    private ListView wishistView, searchedGameList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = (ImageButton)findViewById(R.id.add);
        remove = (ImageButton)findViewById(R.id.remove);
        more = (ImageButton)findViewById(R.id.more);

        newsPage = (LinearLayout)findViewById(R.id.newsPage);
        wishlistPage = (LinearLayout)findViewById(R.id.wishlistPage);
        searchPage = (LinearLayout)findViewById(R.id.searchPage);
        gameToSearch = (EditText)findViewById(R.id.gameToSearch);

        //Button navigation view, 3 pages switcher
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(adapter!=null) adapter.setSelecting(false);
                if(adapter!=null) adapter.notifyDataSetChanged();

                switch (item.getItemId()) {
                    case R.id.navigation_news:
                        wishlistPage.setVisibility(View.GONE);
                        searchPage.setVisibility(View.GONE);
                        newsPage.setVisibility(View.VISIBLE);
                        add.setVisibility(View.GONE);
                        remove.setVisibility(View.GONE);
                        return true;
                    case R.id.navigation_wishlist:
                        newsPage.setVisibility(View.GONE);
                        searchPage.setVisibility(View.GONE);
                        wishlistPage.setVisibility(View.VISIBLE);
                        add.setVisibility(View.GONE);
                        remove.setVisibility(View.VISIBLE);

                        return true;
                    case R.id.navigation_search:
                        newsPage.setVisibility(View.GONE);
                        wishlistPage.setVisibility(View.GONE);
                        searchPage.setVisibility(View.VISIBLE);
                        remove.setVisibility(View.GONE);
                        add.setVisibility(View.VISIBLE);

                        return true;
                }
                return false;
            }
        };

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_wishlist);

        //Add
        wishlistData = new Games();
        wishlistData.add(new Game("Detroit: Become Human","PS4", "Quantic Dream",70.98, 40.98,"test1.jpg"));
        wishlistData.add(new Game("Horizon Zero Dawn","PS4","Guerrilla Games",70.98, 40.98, "test2.jpg"));
        wishlistData.add(new Game("GTA V","PC","Rockstar Games",50.98, 34.98,"test3.jpg"));

        //List with listener (wishlist & searchedGame lists)
        wishistView = (ListView)findViewById(R.id.wishlistView);
        searchedGameList = (ListView)findViewById(R.id.searchedGameList);

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
        adapter = new GameAdapter(wishlistData,this);
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

    public void addToList(View v){
        if(findViewById(R.id.resultsOfSearchLayout).getVisibility() == View.VISIBLE) {
            if (adapter.isSelecting()) {
                //CHECK CHECKED ITEMS
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Vuoi aggiungere N giochi alla wishlist?");
                dialog.setPositiveButton(
                        "Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.setSelecting(false);
                                dialog.cancel();
                            }
                        });

                dialog.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.setSelecting(false);
                                dialog.cancel();
                            }
                        });
                dialog.show();
            } else {
                adapter.setSelecting(true);
            }
        }
    }

    public void removeFromList(View v){
        if (adapter.isSelecting()) {
            //CHECK CHECKED ITEMS
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Vuoi rimuovere N giochi dalla wishlist?");
            dialog.setPositiveButton(
                    "Si",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.setSelecting(false);
                            dialog.cancel();
                        }
                    });

            dialog.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.setSelecting(false);
                            dialog.cancel();
                        }
                    });
            dialog.show();
        } else {
            adapter.setSelecting(true);
        }
    }
}
