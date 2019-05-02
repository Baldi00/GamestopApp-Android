package com.gamestop.android.gamestopapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.w3c.dom.CDATASection;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

public class Game extends GamePreview {

    protected List<String> genres;
    protected String officialSite;
    protected String players;
    protected boolean validForPromotions;

    protected List<Promo> promo;
    protected String description;

    protected Game() {
        // used by importXML
    }

    public Game(String url) throws IOException {

        this.id = url.split("/")[5];

        Document html = Jsoup.connect(url).get();
        Element body = html.body();

        // these three methods are necessary to create a Game
        updateMainInfo(body);
        updateMetadata(body);
        updatePrices(body);

        Log.info("Game", "Game found", title);

        // the following information are not necessary to create a game
        updatePEGI(body);
        updateBonus(body);
        updateDescription(body);

        mkdir();
        updateCover(body);
        updateGallery(body);
    }

    public List<String> getGenres() {
        return genres;
    }

    public boolean hasGenres() {
        return genres != null;
    }

    public String getOfficialSite() {
        return officialSite;
    }

    public boolean hasOfficialSite() {
        return officialSite != null;
    }

    public String getPlayers() {
        return players;
    }

    public boolean hasPlayers() {
        return players != null;
    }

    public boolean isValidForPromotions() {
        return validForPromotions;
    }

    public List<Promo> getPromo() {
        return promo;
    }

    public boolean hasPromo() {
        return promo != null;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        if ( description == null )
            return false;
        return !description.equals("");
    }

    public String getStoreAvailabilityURL () {

        // if a game is pre-orderable can't be in the store
        if ( getPreorderPrice() != null )
            return null;

        return "www.gamestop.it/StoreLocator/Index?productId=" + getId();
    }

    public String getGalleryDirectory() {
        return getGameDirectory() + "gallery/";
    }

    public String getCover() {
        return getGameDirectory() + "cover.jpg";
    }

    public boolean hasCover() {
        return new File(getCover()).exists();
    }

    public String[] getGallery() {

        // salvo i nomi delle immagini
        File file = new File(getGalleryDirectory());
        String[] images = file.list();

        // aggiungo il percorso al nome delle immagini
        for ( int i=0; i<images.length; ++i ){
            images[i] = getGalleryDirectory() + images[i];
        }

        return images;
    }

    public boolean hasGallery() {
        return new File(getGalleryDirectory()).exists();
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

        // I make a copy of all the prices before overwriting them
        Double newPriceCopy = this.newPrice;
        Double usedPriceCopy = this.usedPrice;
        Double preorderPriceCopy = this.preorderPrice;

        // if the prices are removed they don't change
        // example: newPrice is 20€ > then newPrice no longer exist > newPrice is still 20€
        this.newPrice = null;
        this.usedPrice = null;
        this.preorderPrice = null;
        this.olderNewPrices = null;
        this.olderUsedPrices = null;

        for (Element singleVariantDetails : buySection.getElementsByClass("singleVariantDetails")) {

            if (singleVariantDetails.getElementsByClass("singleVariantText").isEmpty()) {
                throw new GameException();
            }

            Element singleVariantText = singleVariantDetails.getElementsByClass("singleVariantText").get(0);

            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Nuovo")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();

                this.newPrice = stringToPrice(price);

                for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {

                    if ( this.olderNewPrices == null ){
                        this.olderNewPrices = new ArrayList<>();
                    }

                    price = olderPrice.text();
                    this.olderNewPrices.add(stringToPrice(price));
                }
            }

            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Usato")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();

                this.usedPrice = stringToPrice(price);

                for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {

                    if ( this.olderUsedPrices == null ){
                        this.olderUsedPrices = new ArrayList<>();
                    }

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

        return changes;
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
                messageURL = "https://www.gamestop.it" + p.get(1).getElementsByTag("a").attr("href");
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

        android.util.Log.d("DESCRIPTION","HERE, " + description);

        if ( !description.equals(descriptionCopy) )
            changes = true;

        return changes;
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
            Log.error("Game", "ID: " + getId() + " - malformed URL", imgUrl);
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

        String imgPath = getGalleryDirectory();

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
                Log.error("Game", "ID: " + getId() + " - malformed URL", imgUrl);
            } catch (IOException ex) {
                Log.error("Game", "cannot download image", imgUrl);
            }
        }

    }

    /**
     *
     * @throws IOException
     */
    public ArrayList<String> update() throws IOException {

        ArrayList<String> notifications = new ArrayList();

        // SAVE INFO BEFORE UPDATE
        Double mainPrice = null;
        Double usedPrice = null;
        boolean preorder = hasPreorderPrice();
        boolean promo = hasPromo();                 //TODO: brutto (verificare numero promo)

        if ( this.hasNewPrice() )
            mainPrice = this.getNewPrice();
        if ( this.hasUsedPrice() )
            usedPrice = this.getUsedPrice();
        if ( this.hasDigitalPrice() )
            mainPrice = this.getDigitalPrice();
        if ( this.hasPreorderPrice() )
            mainPrice = this.getPreorderPrice();

        // DOWNLOAD HTML
        Document html = Jsoup.connect( getURL() ).get();
        Element body = html.body();

        // UPDATE METADATA
        updateMetadata(body);

        // UPDATE PRICES
        if ( updatePrices(body) == true ) {

            //IF THE GAME HAS BEEN RELEASED
            if ( hasPreorderPrice() != preorder ){
                notifications.add("Il gioco è uscito");
            }

            // IF PRICES ARE LOWER
            if ( mainPrice != null ){
                if ( this.hasNewPrice() ){
                    if ( mainPrice > this.getNewPrice() ){
                        notifications.add("Il prezzo del NUOVO è diminuito");
                    }
                }

                if ( this.hasDigitalPrice() ){
                    if ( mainPrice > this.getDigitalPrice() ){
                        notifications.add("Il prezzo della versione DIGITALE è diminuito");
                    }
                }

                if ( this.hasPreorderPrice() ){
                    if ( mainPrice > this.getPreorderPrice() ){
                        notifications.add("Il prezzo del PREORDINE è diminuito");
                    }
                }
            }

            if ( this.hasUsedPrice() && usedPrice!=null){
                if ( usedPrice > this.getUsedPrice() ){
                    notifications.add("Il prezzo dell'USATO è diminuito");
                }
            }

        }

        if ( updateBonus(body) == true ){
            if ( hasPromo() == true && promo == false ){
                notifications.add("Il gioco è in promozione");
            }
        }

        try {
            DirectoryManager.exportGame(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notifications;
    }
}
