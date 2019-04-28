package com.gamestop.android.gamestopapp;

import android.app.Activity;
import android.content.Context;

import java.io.File;

public class DirectoryManager {

    private static final String TEMP_DIR = "tmp/";               // the temporary folder for the app
    private static final String WISHLIST_DIR = "userData/";      // the folder of the saved games
    private static final String APP_DIR = "/data/data/com.gamestop.android.gamestopapp/";      // the folder of the app
    /**
     * @param id id of the game
     * @return  TEMP_DIR if the game is not in the wishlsit
     *          WISHLIST_DIR if the game is in the wishlist
     */
    public static String getDirectory(String id) {

        File file = new File(APP_DIR + WISHLIST_DIR);

        // check if the WISHLIST_DIR exists
        if ( file.exists() ) {

            File[] directories = file.listFiles();

            // search for the directory with the same ID
            for ( int i=0; i<directories.length; ++i ){
                if ( directories[i].getName().equals(id) )
                    return APP_DIR + WISHLIST_DIR;
            }
        }

        return APP_DIR + TEMP_DIR;
    }

    public static String getTempDir(){
        return APP_DIR + TEMP_DIR;
    }

    public static String getWishlistDir(){
        return APP_DIR + WISHLIST_DIR;
    }
}
