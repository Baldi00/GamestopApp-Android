package com.gamestop.android.gamestopapp;

import android.os.AsyncTask;

public class Searcher extends AsyncTask {

    private MainActivity main;
    public Searcher(MainActivity main) {
        this.main = main;
    }

    //Searhc in background the game NOT IMPLEMENTED YET
    @Override
    protected Object doInBackground(Object[] params) {
        //TODO Search the game
        try{
            Thread.sleep(1000);
        }catch(Exception e){};
        return "xxxxxx";
    }

    @Override
    protected void onPostExecute(Object ris) {
        main.onEndSearch(ris);
    }
}
