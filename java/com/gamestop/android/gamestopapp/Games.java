package com.gamestop.android.gamestopapp;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Games extends ArrayList<GamePreview> implements Serializable {

    private static final String SCHEMA_PATH = "schema.xsd";

    @Override
    public boolean add ( GamePreview game ) {

        for ( GamePreview g : this ){
            if ( g.equals(game) ){
                // it's a warning because equals() requires a revision
                Log.warning("Games", "the game already exist", game.getTitle() );
                return false;
            }
        }

        super.add(game);
        Log.info("Games", "game added", game.getPlatform() + ": " + game.getTitle() );

        return true;
    }

    @Override
    public String toString ()
    {
        String str = new String();
        for( int game=0; game<this.size(); ++game ){
            str += this.get(game).toString()+"\n\n";
        }
        return str;
    }

    public void exportBinary(MainActivity main) throws IOException
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DirectoryManager.getWishlistDir(main) + "wishlist.dat"));

        for( int game=0; game<this.size(); ++game ){
            oos.writeObject( this.get(game) );
        }

        Log.info("Games", "exported to binary");
        oos.close();
    }

    public static Games importBinary(MainActivity main) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DirectoryManager.getWishlistDir(main) + "wishlist.dat"));

        Games wishlist = new Games();
        boolean eof = false;

        while(!eof){
            try{
                GamePreview g = (GamePreview) ois.readObject();
                wishlist.add(g);
            }catch(EOFException e){
                eof = true;
            }
        }

        Log.info("Games", "imported from binary");
        return wishlist;
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
