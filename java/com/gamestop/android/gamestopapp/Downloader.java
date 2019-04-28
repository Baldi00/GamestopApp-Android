package com.gamestop.android.gamestopapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class Downloader extends AsyncTask {
    private MainActivity main;

    private String url;

    public Downloader(MainActivity main,String url) {
        this.main = main;
        this.url = url;
    }

    //Downloads in background of the game info, if not present NOT IMPLEMENTED YET
    @Override
    protected Object doInBackground(Object[] params) {
        try {
            return new Game(url, main);
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
