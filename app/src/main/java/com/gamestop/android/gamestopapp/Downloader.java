package com.gamestop.android.gamestopapp;

import android.os.AsyncTask;

import java.io.IOException;

public class Downloader extends AsyncTask {
    private ActivityGamePage main;

    private String url;

    public Downloader(ActivityGamePage main,String url) {
        this.main = main;
        this.url = url;
    }

    //Downloads in background of the game info, if not present NOT IMPLEMENTED YET
    @Override
    protected Object doInBackground(Object[] params) {
        try {
            return new Game(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object ris) {
        main.onEndDownload(ris);
    }
}
