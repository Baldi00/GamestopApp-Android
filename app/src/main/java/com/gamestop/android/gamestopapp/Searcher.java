package com.gamestop.android.gamestopapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

public class Searcher extends AsyncTask {

    private ActivityMain main;
    private String gameToSearch;

    public Searcher(ActivityMain main, String gameToSearch) {
        this.main = main;
        this.gameToSearch = gameToSearch;
    }

    // Search in background the game
    @Override
    protected Object doInBackground(Object[] params) {
        List<GamePreview> gamesFound;
        try {
            gamesFound = GamePreview.searchGame(gameToSearch);
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
