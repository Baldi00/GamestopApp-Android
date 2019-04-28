package com.gamestop.android.gamestopapp;

import android.os.AsyncTask;

public class DownloadGamePage extends AsyncTask {

    private ActivityGamePage main;


    public DownloadGamePage(ActivityGamePage main) {
        this.main = main;
    }

    //Downloads in background of the game info, if not present NOT IMPLEMENTED YET
    @Override
    protected Object doInBackground(Object[] params) {
        return "xxxxxx";
    }

    @Override
    protected void onPostExecute(Object ris) {
        main.onEndDownload(ris);
    }
}
