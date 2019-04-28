package com.gamestop.android.gamestopapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

public class Searcher extends AsyncTask {

    private MainActivity main;
    private String gameToSearch;
    public Searcher(MainActivity main, String gameToSearch) {
        this.main = main;
        this.gameToSearch = gameToSearch;
    }

    //Search in background the game NOT IMPLEMENTED YET
    @Override
    protected Object doInBackground(Object[] params) {
        Games gamesFound;
        try {
            gamesFound = GamePreview.searchGame(gameToSearch,main);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return gamesFound;
    }

    @Override
    protected void onPostExecute(Object ris) {
        main.onEndSearch(ris);
    }
}
