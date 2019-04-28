package com.gamestop.android.gamestopapp;

import android.support.v4.widget.SwipeRefreshLayout;

public class MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout pullToRefresh;
    private MainActivity main;

    public MyOnRefreshListener(SwipeRefreshLayout pullToRefresh, MainActivity main) {
        this.pullToRefresh = pullToRefresh;
        this.main = main;
    }

    //Call the update for the games in wishlist
    @Override
    public void onRefresh() {

        pullToRefresh.setRefreshing(false);
    }
}
