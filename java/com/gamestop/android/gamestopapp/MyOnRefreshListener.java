package com.gamestop.android.gamestopapp;

import android.support.v4.widget.SwipeRefreshLayout;

public class MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout pullToRefresh;

    public MyOnRefreshListener(SwipeRefreshLayout pullToRefresh) {
        this.pullToRefresh = pullToRefresh;
    }

    @Override
    public void onRefresh() {
        pullToRefresh.setRefreshing(false);
    }
}
