package com.gamestop.android.gamestopapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MyOnNavigationItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    //Toolbar buttons
    private ImageButton add,remove;

    //The 3 pages
    private LinearLayout newsPage, wishlistPage, searchPage;

    public MyOnNavigationItemSelectedListener(LinearLayout newsPage, LinearLayout wishlistPage, LinearLayout searchPage, ImageButton add, ImageButton remove) {
        this.newsPage = newsPage;
        this.wishlistPage = wishlistPage;
        this.searchPage = searchPage;
        this.add = add;
        this.remove = remove;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
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
}
