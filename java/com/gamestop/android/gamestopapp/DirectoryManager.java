package com.gamestop.android.gamestopapp;

import android.app.Activity;
import android.content.Context;

import java.io.File;

public class DirectoryManager {

    private static final String TEMP_DIR = "tmp/";               // the temporary folder for the app
    private static final String WISHLIST_DIR = "userData/";      // the folder of the saved games

    /**
     * @param id id of the game
     * @return  TEMP_DIR if the game is not in the wishlsit
     *          WISHLIST_DIR if the game is in the wishlist
     */
    public static String getDirectory( String id, Activity main) {

        String applicationDir = ""+main.getApplicationContext().getFilesDir();

        File file = new File(applicationDir + WISHLIST_DIR);

        // check if the WISHLIST_DIR exists
        if ( file.exists() ) {

            File[] directories = file.listFiles();

            // search for the directory with the same ID
            for ( int i=0; i<directories.length; ++i ){
                if ( directories[i].getName().equals(id) )
                    return applicationDir + WISHLIST_DIR;
            }
        }

        return applicationDir + TEMP_DIR;
    }

    public static String getTempDir(Context main){
        return main.getApplicationContext().getFilesDir() + TEMP_DIR;
    }

    public static String getWishlistDir(Context main){
        return main.getApplicationContext().getFilesDir() + WISHLIST_DIR;
    }
}
