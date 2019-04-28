package com.gamestop.android.gamestopapp;

import java.util.Comparator;

public class GameNewPriceComparator implements Comparator<GamePreview> {

    @Override
    public int compare(GamePreview game1, GamePreview game2) {

        if(game1.getNewPrice()==null || game2.getNewPrice()==null)
            return 0;

        // Games with no new price go at the bottom of the list
        if ( game1.getNewPrice() == null )
            return 1;

        // Games with no new price go at the bottom of the list
        if ( game2.getNewPrice() == null )
            return -1;

        return ((Double) game1.getNewPrice() ).compareTo( game2.getNewPrice() );
    }

}

