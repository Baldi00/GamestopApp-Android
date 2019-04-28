package com.gamestop.android.gamestopapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class Game implements Comparable<Game>, Serializable {

    private ActivityGamePage main;

    private String id;
    private String title;
    private String publisher;
    private String platform;

    private Double newPrice;
    private Double usedPrice;
    private Double preorderPrice;

    private List<Double> olderNewPrices;
    private List<Double> olderUsedPrices;

    private List<String> pegi;
    private List<String> genres;
    private String officialSite;
    private String players;
    private String releaseDate;
    private boolean validForPromotions;

    private List<Promo> promo;
    private String description;

    public Game(String url, ActivityGamePage main) throws IOException {
        this.main = main;
        this.id = url.split("/")[5];

        Document html = Jsoup.connect(url).get();
        Element body = html.body();

        // these three methods are necessary to create a Game
        updateMainInfo(body);

        updateMainInfo(body);

        // elimino tutti gli articoli che non mi interessano
        if ( this.platform.equals("Gadget") )
            throw new IsJustAFuckingGadgetException();
        if ( this.platform.equals("Varie") )
            throw new IsJustAFuckingGadgetException();
        if ( this.platform.equals("Cards") )
            throw new IsJustAFuckingGadgetException();
        if ( this.platform.equals("Telefonia") )
            throw new IsJustAFuckingGadgetException();

        updateMetadata(body);
        updatePrices(body);

        Log.info("Game", "Game found", getTitle() );

        // the following information are not necessary to create a game
        updatePEGI(body);
        updateBonus(body);
        updateDescription(body);

        mkdir();
        updateCover(body);
        updateGallery(body);
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

    public List<Double> getOlderNewPrices() {
        return olderNewPrices;
    }

    public Double getUsedPrice() {
        return usedPrice;
    }

    public List<Double> getOlderUsedPrices() {
        return olderUsedPrices;
    }

    public Double getPreorderPrice() {
        return preorderPrice;
    }

    public List<String> getPegi() {
        return pegi;
    }

    public String getID() {
        return id;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getOfficialSite() {
        return officialSite;
    }

    public String getPlayers() {
        return players;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<Promo> getPromo() {
        return promo;
    }

    public String getDescription() {
        return description;
    }

    public boolean isValidForPromotions() {
        return validForPromotions;
    }

    public static String getURLByID ( int id ) {
        return "http://www.gamestop.it/Platform/Games/" + id;
    }

    public static String getURLByID ( String id ) {
        return "http://www.gamestop.it/Platform/Games/" + id;
    }

    public String getURL() {
        return getURLByID( getID() );
    }

    public String getStoreAvailabilityURL () {

        // if a game is pre-orderable can't be in the store
        if ( getPreorderPrice() != null )
            return null;

        return "www.gamestop.it/StoreLocator/Index?productId=" + getID();
    }

    /**
     * @return the directory where the games' folders are placed
     */
    public static String getDirectory () {
        return "userData/";
    }

    /**
     * @return the game's directory
     */
    public String getGameDirectory () {
        return DirectoryManager.getDirectory(id,main) + id + "/";
    }

    /**
     * @return the game's gallery directory
     */
    public String getGameGalleryDirectory () {
        return getGameDirectory() + "gallery/";
    }

    public boolean hasPromo() {
        return !promo.isEmpty();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
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
        final Game other = (Game) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int compareTo(Game game) {
        return this.getTitle().compareTo(game.getTitle());
    }

    @Override
    public String toString () {
        String str = new String();

        str += "Game {" + "\n ";
        str += "id = " + id + "\n ";
        str += "title = " + title + "\n ";
        str += "publisher = " + publisher + "\n ";
        str += "platform = " + platform + "\n ";

        if ( newPrice != null ){
            str += "newPrice = " + newPrice + "\n ";
            str += "olderNewPrices = " + olderNewPrices + "\n ";
        }

        if ( usedPrice != null ){
            str += "usedPrice = " + usedPrice + "\n ";
            str += "olderUsedPrices = " + olderUsedPrices + "\n ";
        }

        if ( preorderPrice != null ){
            str += "preorderPrice = " + preorderPrice + "\n ";
        }

        if ( promo != null ){
            for ( Promo p : promo )
                str += p + "\n ";
        }

        if ( pegi != null ){
            str += "pegi = " + pegi + "\n ";
        }

        if ( genres != null ){
            str += "genres = " + genres + "\n ";
        }

        if ( officialSite != null ){
            str += "officialSite = " + officialSite + "\n ";
        }

        if ( players != null ){
            str += "players = " + players + "\n ";
        }

        if ( releaseDate != null ){
            str += "releaseDate = " + releaseDate + "\n ";
        }

        str += "validForPromotions = " + validForPromotions + "\n";

        str += "}";

        return str;
    }


    /**
     * returned value checked
     * @param prodTitle
     * @return
     */
    private boolean updateMainInfo(Element prodTitle) {

        boolean changes = false;

        // if the element hasn't got the class name "prodTitle"
        if (!prodTitle.className().equals("prodTitle")) {
            // search for a tag with this class name
            if (prodTitle.getElementsByClass("prodTitle").isEmpty()) {
                throw new GameException();
            }

            prodTitle = prodTitle.getElementsByClass("prodTitle").get(0);
        }

        String tmp = title;
        this.title = prodTitle.getElementsByTag("h1").text();
        if ( !title.equals(tmp) )
            changes = true;

        tmp = publisher;
        this.publisher = prodTitle.getElementsByTag("strong").text();
        if ( !publisher.equals(tmp) )
            changes = true;

        tmp = platform;
        this.platform = prodTitle.getElementsByTag("p").get(0).getElementsByTag("span").text();
        if ( !platform.equals(tmp) )
            changes = true;

        return changes;
    }

    /**
     * returned value checked
     * @param addedDet
     * @return
     */
    private boolean updateMetadata(Element addedDet) {

        // the content is inside "addedDetInfo" which is inside "addedDet"
        // the method use "addedDet" instead of "addedDetInfo" because
        // "addedDet" has an ID instead of a class

        boolean changes = false;

        // if the element hasn't got the id name "addedDet"
        if (!addedDet.id().equals("addedDet")) {
            // search for a tag with this id name
            addedDet = addedDet.getElementById("addedDet");

            if (addedDet == null) {
                throw new GameException();
            }
        }

        List<String> genresCopy = genres;
        if ( genresCopy == null )
            genresCopy = new ArrayList<>();

        this.genres = new ArrayList<>();

        for (Element e : addedDet.getElementsByTag("p")) {
            // important check to avoid IndexOutOfBound Exception
            if (e.childNodeSize() > 1) {

                // set item ID (DEPRECATED)
                if (e.child(0).text().equals("Codice articolo")) {
                    continue;
                }

                // set genre
                if (e.child(0).text().equals("Genere")) {
                    String strGenres = e.child(1).text();           // return example: Action/Adventure
                    for (String genre : strGenres.split("/")) {
                        genres.add(genre);
                    }
                    continue;
                }

                // set official site
                if (e.child(0).text().equals("Sito Ufficiale")) {
                    String officialSiteCopy = officialSite;
                    this.officialSite = e.child(1).getElementsByTag("a").attr("href");
                    if ( !officialSite.equals(officialSiteCopy) )
                        changes = true;
                    continue;
                }

                // set the number of players
                if (e.child(0).text().equals("Giocatori")) {
                    String playersCopy = players;
                    this.players = e.child(1).text();
                    if ( !players.equals(playersCopy) )
                        changes = true;
                    continue;
                }

                // set the release date
                if (e.child(0).text().equals("Rilascio")) {
                    String releaseDateCopy = releaseDate;
                    this.releaseDate = e.child(1).text();
                    this.releaseDate = releaseDate.replace(".", "/");
                    if ( !releaseDate.equals(releaseDateCopy) )
                        changes = true;
                }
            }
        }

        //Log.debug("Game", "Ok");

        // search for a tag with this class name
        if ( !addedDet.getElementsByClass("ProdottoValido").isEmpty() ) {
            this.validForPromotions = true;
            changes = true;
        }

        if ( !genres.equals(genresCopy) )
            changes = true;

        return changes;
    }

    /**
     * returned value checked
     * @param buySection
     * @return
     */
    private boolean updatePrices(Element buySection) {

        boolean changes = false;

        // if the element hasn't got the class name "buySection"
        if (!buySection.className().equals("buySection")) {
            // search for a tag with this class name
            if (buySection.getElementsByClass("buySection").isEmpty()) {
                throw new GameException();
            }

            buySection = buySection.getElementsByClass("buySection").get(0);
        }

        List<Double> olderNewPricesCopy = olderNewPrices;
        List<Double> olderUsedPricesCopy = olderUsedPrices;

        if ( olderNewPricesCopy == null )
            olderNewPricesCopy = new ArrayList<>();

        if ( olderUsedPricesCopy == null )
            olderUsedPricesCopy = new ArrayList<>();

        // I make a copy before overwriting them
        Double newPriceCopy = this.newPrice;
        Double usedPriceCopy = this.usedPrice;
        Double preorderPriceCopy = this.preorderPrice;

        // if the prices are removed they don't change
        // example: newPrice is 20€ > then newPrice no longer exist > newPrice is still 20€
        this.newPrice = null;
        this.usedPrice = null;
        this.preorderPrice = null;

        this.olderNewPrices = new ArrayList<>();
        this.olderUsedPrices = new ArrayList<>();

        for (Element singleVariantDetails : buySection.getElementsByClass("singleVariantDetails")) {

            if (singleVariantDetails.getElementsByClass("singleVariantText").isEmpty()) {
                throw new GameException();
            }

            Element singleVariantText = singleVariantDetails.getElementsByClass("singleVariantText").get(0);

            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Nuovo")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();

                this.newPrice = stringToPrice(price);

                for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {
                    price = olderPrice.text();
                    this.olderNewPrices.add(stringToPrice(price));
                }
            }

            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Usato")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();

                this.usedPrice = stringToPrice(price);

                for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {
                    price = olderPrice.text();
                    this.olderUsedPrices.add(stringToPrice(price));
                }
            }

            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Prenotazione")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();

                this.preorderPrice = stringToPrice(price);
            }
        }

        if ( newPrice != null && !newPrice.equals(newPriceCopy) )
            changes = true;

        if ( usedPrice != null && !usedPrice.equals(usedPriceCopy) )
            changes = true;

        if ( preorderPrice != null && !preorderPrice.equals(preorderPriceCopy) )
            changes = true;

        if ( !olderNewPricesCopy.equals(olderNewPrices) )
            changes = true;

        if ( !olderUsedPricesCopy.equals(olderUsedPrices) )
            changes = true;

        return changes;
    }

    private double stringToPrice(String price) {
        price = price.replace(".", "");     // <-- to handle prices over 999,99€ like 1.249,99€
        price = price.replace(',', '.');    // <-- to convert the price in a string which can be parsed
        price = price.replace("€", "");     // <-- remove unecessary characters
        price = price.replace("CHF", "");   // <-- remove unecessary characters
        price = price.trim();               // <-- remove remaning spaces

        return Double.parseDouble(price);
    }

    /**
     * returned value checked
     * @param ageBlock
     * @return
     */
    private boolean updatePEGI(Element ageBlock) {

        boolean changes = false;

        // if the element hasn't got the class name "ageBlock"
        if (!ageBlock.className().equals("ageBlock")) {
            // search for a tag with this class name
            if (ageBlock.getElementsByClass("ageBlock").isEmpty()) {
                return changes;
            }

            ageBlock = ageBlock.getElementsByClass("ageBlock").get(0);
        }

        // init the array
        List<String> pegiCopy = pegi;
        if ( pegiCopy == null )
            pegiCopy =  new ArrayList<>();

        this.pegi = new ArrayList<>();

        for (Element e : ageBlock.getAllElements()) {
            String str = e.attr("class");

            if (str.equals("pegi18"))   { this.pegi.add("pegi18"); continue; }
            if (str.equals("pegi16"))   { this.pegi.add("pegi16"); continue; }
            if (str.equals("pegi12"))   { this.pegi.add("pegi12"); continue; }
            if (str.equals("pegi7"))    { this.pegi.add("pegi7"); continue; }
            if (str.equals("pegi3"))    { this.pegi.add("pegi3"); continue; }

            if (str.equals("ageDescr BadLanguage"))     { this.pegi.add("bad-language"); continue; }
            if (str.equals("ageDescr violence"))        { this.pegi.add("violence"); continue; }
            if (str.equals("ageDescr online"))          { this.pegi.add("online"); continue; }
            if (str.equals("ageDescr sex"))             { this.pegi.add("sex"); continue; }
            if (str.equals("ageDescr fear"))            { this.pegi.add("fear"); continue; }
            if (str.equals("ageDescr drugs"))           { this.pegi.add("drugs"); continue; }
            if (str.equals("ageDescr discrimination"))  { this.pegi.add("discrimination"); continue; }
            if (str.equals("ageDescr gambling"))        { this.pegi.add("gambling"); }
        }

        if ( !pegi.equals(pegiCopy) )
            changes = true;

        return changes;
    }

    /**
     * returned value checked
     * @param bonusBlock
     * @return
     */
    private boolean updateBonus(Element bonusBlock) {

        boolean changes = false;

        // if the element hasn't got the id name "bonusBlock"
        if (!bonusBlock.id().equals("bonusBlock")) {
            // search for a tag with this id name
            bonusBlock = bonusBlock.getElementById("bonusBlock");

            if (bonusBlock == null) {
                return changes;
            }
        }

        List<Promo> promoCopy = promo;
        promo = new ArrayList<>();

        for (Element prodSinglePromo : bonusBlock.getElementsByClass("prodSinglePromo")) {

            Elements h4 = prodSinglePromo.getElementsByTag("h4");
            Elements p = prodSinglePromo.getElementsByTag("p");

            // possible NullPointerException
            // per il momento non voglio correggere questo errore perchè
            // mi servono molti casi di test
            String header = h4.text();
            String validity = p.get(0).text();
            String message = null;
            String messageURL = null;

            // se la promozione contiene un link per personalizzare l'acquisto
            if ( p.size() > 1 ) {
                message = p.get(1).text();
                messageURL = "www.gamestop.it" + p.get(1).getElementsByTag("a").attr("href");
            }

            promo.add(new Promo(header, validity, message, messageURL));
        }

        if ( !promo.equals(promoCopy) )
            changes = true;

        return changes;
    }

    /**
     * returned value checked
     * @param prodDesc
     * @return
     */
    private boolean updateDescription(Element prodDesc) {

        boolean changes = false;

        // if the element hasn't got the id name "addedDet"
        if (!prodDesc.id().equals("prodDesc")) {
            // search for a tag with this id name
            prodDesc = prodDesc.getElementById("prodDesc");

            if (prodDesc == null) {
                return changes;
            }
        }

        String descriptionCopy = this.description;
        if ( descriptionCopy == null )
            descriptionCopy = new String();

        this.description = new String();

        for (Element e : prodDesc.getElementsByTag("p")) {
            for (TextNode tn : e.textNodes()) {
                description += tn.text() + "\n";
            }
        }

        if ( !description.equals(descriptionCopy) )
            changes = true;

        return changes;
    }

    /**
     * Create the game folder
     */
    private void mkdir() {
        // create userData folder if doesn't exist
        File dir = new File(DirectoryManager.getTempDir(main));

        if (!dir.exists()) {
            dir.mkdir();
        }

        // create the game folder if doesn't exist
        dir = new File( getGameDirectory() );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     *
     * @param prodImgMax
     */
    private void updateCover(Element prodImgMax) {

        // if the element hasn't got the class name "prodImg max"
        if (!prodImgMax.className().equals("prodImg max")) {
            // search for a tag with this class name
            if (prodImgMax.getElementsByClass("prodImg max").isEmpty()) {
                return;
            }

            prodImgMax = prodImgMax.getElementsByClass("prodImg max").get(0);
        }

        String imgUrl = prodImgMax.attr("href");
        String imgPath = getGameDirectory();

        try {
            downloadImage("cover.jpg", imgUrl, imgPath);
        } catch ( MalformedURLException ex ) {
            Log.error("Game", "ID: " + getID() + " - malformed URL", imgUrl);
        } catch (IOException ex) {
            Log.error("Game", "cannot download cover", imgUrl);
        }
    }

    /**
     *
     * @param mediaImages
     */
    private void updateGallery(Element mediaImages) {

        // if the element hasn't got the class name "mediaImages"
        if (!mediaImages.className().equals("mediaImages")) {
            // search for a tag with this class name
            if (mediaImages.getElementsByClass("mediaImages").isEmpty()) {
                return;
            }

            mediaImages = mediaImages.getElementsByClass("mediaImages").get(0);
        }

        String imgPath = getGameGalleryDirectory();

        File dir = new File(imgPath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        for (Element e : mediaImages.getElementsByTag("a")) {
            String imgUrl = e.attr("href");
            if (imgUrl.equals("")) {
                // this can handle very rare cases of malformed HTMLs
                // ex. https://www.gamestop.it/Varie/Games/95367/cambio-driving-force-per-volanti-g29-e-g920
                imgUrl = e.getElementsByTag("img").get(0).attr("src");
            }

            String imgName = imgUrl.split("/")[6];

            try {
                downloadImage(imgName, imgUrl, imgPath);
            } catch ( MalformedURLException ex ) {
                Log.error("Game", "ID: " + getID() + " - malformed URL", imgUrl);
            } catch (IOException ex) {
                Log.error("Game", "cannot download image", imgUrl);
            }
        }

    }

    private void downloadImage(String name, String imgUrl, String imgPath) throws MalformedURLException, IOException {
        imgPath = imgPath + name;
        File f = new File(imgPath);

        // if the image already exists
        if (f.exists()) {
            Log.warning("Game", "img already exists", imgPath);
            return;
        }

        /*InputStream in = new URL(imgUrl).openStream();
        Files.copy(in, Paths.get(imgPath));*/

        Bitmap image = getBitmapFromURL(imgUrl);


        try (FileOutputStream out = new FileOutputStream(imgPath)) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        } catch (IOException e) {
            e.printStackTrace();
        }



        Log.info("Game", "image downloaded", imgUrl);
    }

    /**
     *
     * @throws IOException
     */
    public void update() throws IOException {

        Document html = Jsoup.connect( getURL() ).get();
        Element body = html.body();

        if ( updateMetadata(body) == true ){
            Log.debug("Game", getTitle() + ": Metadata have changed");
        }

        if ( updatePrices(body) == true ) {
            Log.debug("Game", getTitle() + ": Prices have changed");
        }

        if ( updateBonus(body) == true ){
            Log.debug("Game", getTitle() + ": Promo has changed");
        }

    }

    public void exportBinary() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream( main.getApplicationContext().getFilesDir() + getGameDirectory() + "data.dat"));

        oos.writeObject( this );

        Log.info("Game", "exported to binary");
        oos.close();
    }

    public static Game importBinary( String path ) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));

        Game game = null;
        boolean eof = false;

        while(!eof){
            try{
                game = (Game)ois.readObject();
            }catch(EOFException e){
                eof = true;
            }
        }

        Log.info("Game", "imported from binary");
        return game;
    }

    //Added by Andrea
    public Bitmap getBitmapFromURL(String src) {
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

    public String getCover(){
        return getGameDirectory() + "cover.jpg";
    }
}
