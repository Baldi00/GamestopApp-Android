package com.gamestop.android.gamestopapp;

import android.os.AsyncTask;

public class Searcher extends AsyncTask {

    private MainActivity main;
    public Searcher(MainActivity main) {
        this.main = main;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try{
            Thread.sleep(1000);
        }catch(Exception e){};
        return "xxxxxx"; // restitisco il risultato
    }

    @Override
    protected void onPostExecute(Object ris) {
        main.onEndSearch(ris);
    }
}
