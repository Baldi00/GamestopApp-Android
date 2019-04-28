package com.gamestop.android.gamestopapp;

import android.os.AsyncTask;

public class DownloadGamePage extends AsyncTask {

    private ActivityGamePage main;
    public DownloadGamePage(ActivityGamePage main) {
        this.main = main;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try{
            Thread.sleep(3000);
        }catch(Exception e){};
        return "xxxxxx"; // restitisco il risultato
    }

    @Override
    protected void onPostExecute(Object ris) {
        main.onEndDownload(ris);
    }
}
