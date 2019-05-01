package com.gamestop.android.gamestopapp;

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Games extends ArrayList<GamePreview> {

    @Override
    public boolean add ( GamePreview game ) {

        if ( game == null ){
            Log.info("Games", "impossible to add the game to the list. The object is null");
            return false;
        }

        for ( GamePreview g : this ){
            if ( g.equals(game) ){
                Log.warning("Games", "the game already exist", game.getTitle() );
                return false;
            }
        }

        super.add(game);
        Log.info("Games", "game added", "["+game.getId()+"]" + "["+game.getPlatform()+"]" + " - \"" + game.getTitle() + "\"" );

        return true;
    }

    @Override
    public String toString () {
        String str = new String();
        for( int game=0; game<this.size(); ++game ){
            str += this.get(game).toString()+"\n\n";
        }
        return str;
    }

    public void sortbyName () {
        Collections.sort( this );
    }

    public void sortByPlatform () {
        Collections.sort( this, new GamePlatformComparator() );
    }

    public void sortByNewPrice () {
        Collections.sort( this, new GameNewPriceComparator() );
    }

    public void sortByUsedPrice () {
        Collections.sort( this, new GameUsedPriceComparator() );
    }

    public void sortByReleaseDate () {
        Collections.sort( this, new GameReleaseDateComparator() );
    }

}
