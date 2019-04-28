package com.gamestop.android.gamestopapp;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private String title;                   // it contains the title of the game
    private String url;                     // it contains the URL of the Gamestop's page
    private String publisher;               // it contains the game's publisher name
    private String platform;                // it contains the console where the game run (can be also a Gadget)
    private String cover;                   // it contains the title formatted with UTF-8. It's used to pick the image from the folder "usedData/covers"
    private double newPrice;                // it's the price for a new game
    private List<Double> olderNewPrices;    // it's the old price for a new game (in rare cases there are two or more older prices)
    private double usedPrice;               // it's the price for a used game
    private List<Double> olderUsedPrices;   // it's the old price for a used game (in rare cases there are two or more older prices)
    private List<String> pegi;              // it's a list containing all the types of PEGI a Game has
    private List<String> genres;            // it's a list containing all the genres a Game has
    private String officialSite;            // it contains the URL of the official site of the Game
    private short players;                  // it contains the number of players that can play the game at the same time
    private String releaseDate;             // it contains the release date of the game

    private Game() {
        this.title = null;
        this.url = null;
        this.publisher = null;
        this.platform = null;
        this.cover = null;
        this.newPrice = -1;
        this.olderNewPrices = new ArrayList<>();
        this.usedPrice = -1;
        this.olderUsedPrices = new ArrayList<>();
        this.pegi = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.officialSite = null;
        this.players = -1;
        this.releaseDate = null;
    }

    public Game(String title, String platform, String publisher, double newPrice, double usedPrice, String cover){
        this.title = title;
        this.platform = platform;
        this.publisher = publisher;
        this.newPrice = newPrice;
        this.usedPrice = usedPrice;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public String getPlatform() {
        return platform;
    }

    public String getPublisher() {
        return publisher;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public double getUsedPrice() {
        return usedPrice;
    }

    public String getCover(){
        return cover;
    }
}
