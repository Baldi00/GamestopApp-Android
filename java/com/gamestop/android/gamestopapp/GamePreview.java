package com.gamestop.android.gamestopapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GamePreview implements Comparable<GamePreview> {

    protected String id;
    protected String title;
    protected String publisher;
    protected String platform;

    protected Double newPrice;
    protected Double usedPrice;
    protected Double preorderPrice;
    protected Double digitalPrice;
    protected List<Double> olderNewPrices;
    protected List<Double> olderUsedPrices;

    protected List<String> pegi;
    protected String releaseDate;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPlatform() {
        return platform;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    public boolean hasNewPrice() {
        return newPrice != null;
    }

    public Double getUsedPrice() {
        return usedPrice;
    }

    public boolean hasUsedPrice() {
        return usedPrice != null;
    }

    public Double getPreorderPrice() {
        return preorderPrice;
    }

    public boolean hasPreorderPrice() {
        return preorderPrice != null;
    }

    public Double getDigitalPrice() {
        return digitalPrice;
    }

    public boolean hasDigitalPrice() {
        return digitalPrice != null;
    }

    public List<Double> getOlderNewPrices() {
        return olderNewPrices;
    }

    public boolean hasOlderNewPrices() {
        return olderNewPrices != null;
    }

    public List<Double> getOlderUsedPrices() {
        return olderUsedPrices;
    }

    public boolean hasOlderUsedPrices() {
        return olderUsedPrices != null;
    }

    public List<String> getPegi() {
        return pegi;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public String getURL() {
        return getURLByID(id);
    }

    public static String getURLByID ( String id ) {
        return "http://www.gamestop.it/Platform/Games/" + id;
    }

    public String getGameDirectory(Activity main) {
        return DirectoryManager.getDirectory(id, main) + id + "/";
    }

    public String getCover(Activity main) {
        return getGameDirectory(main) + "preview.jpg";
    }

    public boolean hasCover(Activity main) {
        return new File(getCover(main)).exists();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GamePreview other = (GamePreview) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {

        String str = new String();
        str += "GamePreview{\n";

        if ( id != null )
            str += " id = " + id + "\n";

        if ( title != null )
            str += " title = " + title + "\n";

        if ( publisher != null )
            str += " publisher = " + publisher + "\n";

        if ( platform != null )
            str += " platform = " + platform + "\n";

        if ( newPrice != null )
            str += " newPrice = " + newPrice + "\n";

        if ( usedPrice != null )
            str += " usedPrice = " + usedPrice + "\n";

        if ( preorderPrice != null )
            str += " preorderPrice = " + preorderPrice + "\n";

        if ( digitalPrice != null )
            str += " digitalPrice = " + digitalPrice + "\n";

        if ( olderNewPrices != null )
            str += " olderNewPrices = " + olderNewPrices + "\n";

        if ( olderUsedPrices != null )
            str += " olderUsedPrices = " + olderUsedPrices + "\n";

        if ( pegi != null )
            str += " pegi = " + pegi + "\n";

        if ( releaseDate != null )
            str += " releaseDate = " + releaseDate + "\n";

        str += "}";
        return str;
    }

    @Override
    public int compareTo(GamePreview gamePreview) {
        return title.compareTo(gamePreview.getTitle());
    }

    public static List<GamePreview> searchGame(String searchedGameName, MainActivity main) throws UnsupportedEncodingException, IOException {

        String url = "https://www.gamestop.it/SearchResult/QuickSearch?q=" + URLEncoder.encode(searchedGameName, "UTF-8");

        Document doc = Jsoup.connect(url).get();
        Element body = doc.body();

        Elements gamesList = body.getElementsByClass("singleProduct");
        Log.info("GamePreview", "search completed", gamesList.size()+" results" );

        // if there are no games
        if ( gamesList.isEmpty() ){
            return null;
        }

        List<GamePreview> searchedGames = new ArrayList();

        for ( Element game : gamesList )
        {
            GamePreview gamePreview = new GamePreview();

            gamePreview.id = game.getElementsByClass("prodImg").get(0).attr("href").split("/")[3];
            gamePreview.title = game.getElementsByTag("h3").get(0).text();
            gamePreview.publisher = game.getElementsByTag("h4").get(0).getElementsByTag("strong").text();
            gamePreview.platform = game.getElementsByTag("h4").get(0).textNodes().get(0).text().trim();

            Elements e = game.getElementsByClass("buyNew");
            if ( !e.isEmpty() ){
                Elements prices = e.get(0).getElementsByTag("em");

                // if there's just one price
                // NB: <em> tag is present only if there are multiple prices
                if ( prices.isEmpty() ){
                    String price = e.get(0).text();
                    gamePreview.newPrice = stringToPrice(price);
                }

                // if more than one price is present
                if ( prices.size() > 1 )
                    gamePreview.olderNewPrices = new ArrayList<>();

                // memorize the prices
                for ( int i=0; i<prices.size(); ++i ){
                    String price = prices.get(i).text();

                    if ( i==0 ){
                        gamePreview.newPrice = stringToPrice(price);
                    } else {
                        gamePreview.olderNewPrices.add(stringToPrice(price));
                    }
                }
            }

            e = game.getElementsByClass("buyUsed");
            if ( !e.isEmpty() ){
                Elements prices = e.get(0).getElementsByTag("em");

                // if there's just one price
                // NB: <em> tag is present only if there are multiple prices
                if ( prices.isEmpty() ){
                    String price = e.get(0).text();
                    gamePreview.usedPrice = stringToPrice(price);
                }

                // if more than one price is present
                if ( prices.size() > 1 ) {
                    gamePreview.olderUsedPrices = new ArrayList<>();

                    // memorize the prices
                    for ( int i=0; i<prices.size(); ++i ){
                        String price = prices.get(i).text();

                        if ( i==0 ){
                            gamePreview.usedPrice = stringToPrice(price);
                        } else {
                            gamePreview.olderUsedPrices.add(stringToPrice(price));
                        }
                    }
                }
            }

            e = game.getElementsByClass("buyPresell");
            if ( !e.isEmpty() ){
                String price = e.get(0).text();
                gamePreview.preorderPrice = stringToPrice(price);
            }

            e = game.getElementsByClass("buyDLC");
            if ( !e.isEmpty() ){
                String price = e.get(0).text();
                gamePreview.digitalPrice = stringToPrice(price);
            }

            gamePreview.pegi = new ArrayList<>();
            gamePreview.pegi.add( game.getElementsByTag("p").get(0).text() );
            gamePreview.releaseDate = game.getElementsByTag("li").get(0).text().split(": ")[1];

            // create the necessary directories
            gamePreview.mkdir(main);

            // download the cover
            String imageUrl = game.getElementsByClass("prodImg").get(0).getElementsByTag("img").get(0).attr("data-llsrc");
            String imageName = imageUrl.split("/")[6];
            downloadImage("", imageUrl, gamePreview.getCover(main));

            searchedGames.add(gamePreview);
        }

        return searchedGames;
    }

    protected static double stringToPrice(String price) {

        // example "Nuovo 19.99€"
        price = price.replaceAll("[^0-9.,]","");    // remove all the characters except for numbers, ',' and '.'
        price = price.replace(".", "");             // to handle prices over 999,99€ like 1.249,99€
        price = price.replace(',', '.');            // to convert the price in a string that can be parsed

        /* OLD
        price = price.split(" ")[1];        // <-- example "Nuovo 19.99€"
        price = price.replace(".", "");     // <-- to handle prices over 999,99€ like 1.249,99€
        price = price.replace(',', '.');    // <-- to convert the price in a string that can be parsed
        price = price.replace("€", "");     // <-- remove unecessary characters
        price = price.replace("CHF", "");   // <-- remove unecessary characters
        price = price.trim();               // <-- remove remaning spaces
        */

        return Double.parseDouble(price);
    }

    protected static void downloadImage(String name, String imgUrl, String imgPath) throws MalformedURLException, IOException {
        imgPath = imgPath + name;
        File f = new File(imgPath);

        // if the image already exists
        if (f.exists()) {
            Log.warning("GamePreview", "img already exists", imgPath);
            return;
        }

        Bitmap image = getBitmapFromURL(imgUrl);


        try (FileOutputStream out = new FileOutputStream(imgPath)) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.info("GamePreview", "image downloaded", imgUrl);
    }

    protected static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void mkdir(Activity main) {
        // create userData folder if doesn't exist
        File dir = new File(DirectoryManager.getTempDir(main));

        if (!dir.exists()) {
            dir.mkdir();
        }

        // create the game folder if doesn't exist
        dir = new File( getGameDirectory(main) );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

}