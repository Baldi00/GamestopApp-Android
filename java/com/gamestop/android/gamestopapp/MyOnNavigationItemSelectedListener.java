package com.gamestop.android.gamestopapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MyOnNavigationItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    //Toolbar buttons
    private ImageButton goToSearch;

    //The 3 pages
    private LinearLayout newsPage, wishlistPage, searchPage;

    private MainActivity main;

    public MyOnNavigationItemSelectedListener(LinearLayout newsPage, LinearLayout wishlistPage, LinearLayout searchPage, ImageButton goToSearch, MainActivity main) {
        this.newsPage = newsPage;
        this.wishlistPage = wishlistPage;
        this.searchPage = searchPage;
        this.goToSearch = goToSearch;
        this.main = main;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_news:
                wishlistPage.setVisibility(View.GONE);
                searchPage.setVisibility(View.GONE);
                newsPage.setVisibility(View.VISIBLE);
                goToSearch.setVisibility(View.GONE);
                return true;
            case R.id.navigation_wishlist:
                newsPage.setVisibility(View.GONE);
                searchPage.setVisibility(View.GONE);
                wishlistPage.setVisibility(View.VISIBLE);
                goToSearch.setVisibility(View.VISIBLE);
                return true;
            case R.id.navigation_search:
                newsPage.setVisibility(View.GONE);
                wishlistPage.setVisibility(View.GONE);
                searchPage.setVisibility(View.VISIBLE);
                goToSearch.setVisibility(View.GONE);
                return true;
        }
        return false;
    }
}
