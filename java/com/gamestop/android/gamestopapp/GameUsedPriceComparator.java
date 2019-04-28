package com.gamestop.android.gamestopapp;


import java.util.Comparator;

public class GameUsedPriceComparator implements Comparator<GamePreview> {

    @Override
    public int compare(GamePreview game1, GamePreview game2) {

        // Games with no new price go at the bottom of the list
        if ( game1.getUsedPrice() == null )
            return 1;

        // Games with no new price go at the bottom of the list
        if ( game2.getUsedPrice() == null )
            return -1;

        return ( (Double) game1.getUsedPrice() ).compareTo( game2.getUsedPrice() );
    }

}
