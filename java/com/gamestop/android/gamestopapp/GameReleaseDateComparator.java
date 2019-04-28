package com.gamestop.android.gamestopapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class GameReleaseDateComparator implements Comparator<GamePreview> {

    @Override
    public int compare(GamePreview game1, GamePreview game2) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d1, d2;

        try {
            d1 = sdf.parse( game1.getReleaseDate() );
        } catch (ParseException pe) {
            Log.error( game1.getTitle(), "wrong date format");
            return 0;
        }

        try {
            d2 = sdf.parse( game2.getReleaseDate() );
        } catch (ParseException ex) {
            Log.error( game2.getTitle(), "wrong date format");
            return 0;
        }

        return d1.compareTo(d2);
    }

}

