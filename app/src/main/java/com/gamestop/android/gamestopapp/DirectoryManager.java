package com.gamestop.android.gamestopapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DirectoryManager {
    private static final String APP_DIR = "/data/data/com.gamestop.android.gamestopapp/";      // the folder of the app
    private static final String GAMES_DIRECTORY = APP_DIR + "userdata/";
    private static final String WISHLIST = GAMES_DIRECTORY + "data.csv";
    private static final String SCHEMA_GAME = APP_DIR + "Game.xsd";

    public static void mkdir(){
        File dir = new File(GAMES_DIRECTORY);

        if(!dir.exists()){
            dir.mkdir();
        }
    }

    public static String getGamesDirectory() {
        return GAMES_DIRECTORY;
    }

    public static File getWishlist() {
        return new File(WISHLIST);
    }

    public static String getGameDirectory(String gameId){
        return getGamesDirectory() + gameId + "/";
    }

    public static String getGameGalleryDirectory(String gameId){
        return getGameDirectory(gameId) + "gallery/";
    }

    public static File getGameXML(String gameId){
        return new File(getGameDirectory(gameId)+"data.xml");
    }

    public static String getAppDir() {
        return APP_DIR;
    }

    // IMPORT/EXPORT METHODS FOR GAME CLASS -----------------------------------

    public static void exportGame(Game game) throws Exception  {

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        doc.appendChild( exportGame(game, doc) );

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        File f = getGameXML(game.getId());

        transformer.transform( new DOMSource(doc), new StreamResult(f) );

        // check the XML
        validateGame(f);

        Log.info("DirectoryManager", "Game exported successfully", game.getId() + ": \"" + game.getTitle() + "\"");
    }

    public static Element exportGame(Game game, Document doc){

        Element gameElement = doc.createElement("game");

        gameElement.setAttribute("id", game.getId());

        //Element Title
        Element elementTitle = doc.createElement("title");
        CDATASection cdataTitle = doc.createCDATASection(game.getTitle());
        elementTitle.appendChild(cdataTitle);
        gameElement.appendChild(elementTitle);


        //Element Publisher
        Element elementPublisher = doc.createElement("publisher");
        CDATASection cdataPublisher = doc.createCDATASection(game.getPublisher());
        elementPublisher.appendChild(cdataPublisher);
        gameElement.appendChild(elementPublisher);

        //Element Platform
        Element elementPlatform = doc.createElement("platform");
        CDATASection cdataPlatform = doc.createCDATASection(game.getPlatform());
        elementPlatform.appendChild(cdataPlatform);
        gameElement.appendChild(elementPlatform);

        //Element Prices
        Element prices = doc.createElement("prices");

        //Element NewPrice
        if( game.hasNewPrice() ){
            Element elementNewPrice = doc.createElement("newPrice");
            elementNewPrice.setTextContent(String.valueOf(game.getNewPrice()));
            prices.appendChild(elementNewPrice);
        }

        //Element OlderNewPrices
        if( game.hasOlderNewPrices() ){
            Element elementOlderNewPrice = doc.createElement("olderNewPrices");

            for(Double price : game.getOlderNewPrices()){
                Element elementPrice = doc.createElement("price");
                elementPrice.setTextContent(price.toString());
                elementOlderNewPrice.appendChild(elementPrice);
            }

            prices.appendChild(elementOlderNewPrice);
        }

        //Element UsedPrice
        if( game.hasUsedPrice() ){
            Element elementUsedPrice = doc.createElement("usedPrice");
            elementUsedPrice.setTextContent(String.valueOf(game.getUsedPrice()));
            prices.appendChild(elementUsedPrice);
        }

        //Element OlderUsedPrices
        if( game.hasOlderUsedPrices() ){
            Element elementOlderUsedPrice = doc.createElement("olderUsedPrices");

            for(Double price : game.getOlderUsedPrices()){
                Element elementPrice = doc.createElement("price");
                elementPrice.setTextContent(price.toString());
                elementOlderUsedPrice.appendChild(elementPrice);
            }

            prices.appendChild(elementOlderUsedPrice);
        }

        //Element PreOrderPrice
        if( game.hasPreorderPrice() ){
            Element elementPreorderPrice = doc.createElement("preorderPrice");
            elementPreorderPrice.setTextContent(String.valueOf(game.getPreorderPrice()));
            prices.appendChild(elementPreorderPrice);
        }

        //Element DigitalPrice
        if( game.hasDigitalPrice() ){
            Element elementDigitalPrice = doc.createElement("digitalPrice");
            elementDigitalPrice.setTextContent(String.valueOf(game.getDigitalPrice()));
            prices.appendChild(elementDigitalPrice);
        }

        gameElement.appendChild(prices);

        //Element Pegi
        if( game.hasPegi() ){
            Element elementPegiList = doc.createElement("pegi");
            for(String p : game.getPegi()){
                Element elementPegi = doc.createElement("type");
                elementPegi.setTextContent(p);
                elementPegiList.appendChild(elementPegi);
            }
            gameElement.appendChild(elementPegiList);
        }

        //Element Genres
        if( game.hasGenres() ){
            Element elementGenres = doc.createElement("genres");

            for(String genre : game.getGenres()){
                Element elementGenre = doc.createElement("genre");
                CDATASection cdataGenre = doc.createCDATASection(genre);
                elementGenre.appendChild(cdataGenre);
                elementGenres.appendChild(elementGenre);
            }

            gameElement.appendChild(elementGenres);
        }

        //Element OfficialSite
        if( game.hasOfficialSite() ){
            Element elementOfficialSite = doc.createElement("officialSite");
            CDATASection cdataOfficialSite = doc.createCDATASection(game.getOfficialSite());
            elementOfficialSite.appendChild(cdataOfficialSite);
            gameElement.appendChild(elementOfficialSite);
        }

        //Element Players
        if( game.hasPlayers() ){
            Element elementPlayers = doc.createElement("players");
            CDATASection cdataPlayers = doc.createCDATASection(game.getPlayers());
            elementPlayers.appendChild(cdataPlayers);
            gameElement.appendChild(elementPlayers);
        }

        //Element ReleaseDate
        Element elementReleaseDate = doc.createElement("releaseDate");
        elementReleaseDate.setTextContent(game.getReleaseDate());
        gameElement.appendChild(elementReleaseDate);

        // promo
        if( game.hasPromo() ){
            Element elementPromos = doc.createElement("promos");

            for(Promo p : game.getPromo()){
                Element elementPromo = doc.createElement("promo");

                Element elementHeader = doc.createElement("header");
                CDATASection cdataHeader = doc.createCDATASection(p.getHeader());
                elementHeader.appendChild(cdataHeader);
                elementPromo.appendChild(elementHeader);

                Element elementValidity = doc.createElement("validity");
                CDATASection cdataValidity = doc.createCDATASection(p.getValidity());
                elementValidity.appendChild(cdataValidity);
                elementPromo.appendChild(elementValidity);

                if(p.getMessage() != null){
                    Element elementMessage = doc.createElement("message");
                    CDATASection cdataMessage = doc.createCDATASection(p.getMessage());
                    elementMessage.appendChild(cdataMessage);
                    elementPromo.appendChild(elementMessage);

                    Element elementMessageURL = doc.createElement("messageURL");
                    CDATASection cdataMessageURL = doc.createCDATASection(p.getMessageURL());
                    elementMessageURL.appendChild(cdataMessageURL);
                    elementPromo.appendChild(elementMessageURL);
                }

                elementPromos.appendChild(elementPromo);
            }

            gameElement.appendChild(elementPromos);
        }

        //Element Description
        if( game.hasDescription() ){
            Element elementDescription = doc.createElement("description");
            CDATASection cdataDescription = doc.createCDATASection(game.getDescription());
            elementDescription.appendChild(cdataDescription);
            gameElement.appendChild(elementDescription);
        }

        //Element ValidForPromos
        if( game.isValidForPromotions() ){
            Element elementValidForPromo = doc.createElement("validForPromo");
            elementValidForPromo.setTextContent(""+game.isValidForPromotions());
            gameElement.appendChild(elementValidForPromo);
        }

        return gameElement;
    }

    public static Game importGame(String gameId) throws Exception  {

        File f = getGameXML(gameId);      // need revision

        // check the XML
        validateGame(f);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        return importGame(doc);
    }

    private static Game importGame(Document doc){

        Game game = new Game();

        Element gameElement = doc.getDocumentElement();

        game.id = gameElement.getAttribute("id");

        game.title = gameElement.getElementsByTagName("title").item(0).getChildNodes().item(0).getTextContent();
        game.publisher = gameElement.getElementsByTagName("publisher").item(0).getChildNodes().item(0).getTextContent();
        game.platform = gameElement.getElementsByTagName("platform").item(0).getChildNodes().item(0).getTextContent();

        Element prices = (Element)gameElement.getElementsByTagName("prices").item(0);

        //NEW PRICE
        org.w3c.dom.NodeList nl = prices.getElementsByTagName("newPrice");
        if(nl.getLength() > 0){
            Element newPrice = (Element)nl.item(0);
            game.newPrice = Double.valueOf(newPrice.getTextContent());
        }

        //OLDER NEW PRICES
        nl = prices.getElementsByTagName("olderNewPrices");
        if(nl.getLength() > 0){
            game.olderNewPrices = new ArrayList();
            Element olderNewPrices = (Element)nl.item(0);
            nl = olderNewPrices.getElementsByTagName("price");
            for(int i = 0; i<nl.getLength(); i++){
                Element elementPrice = (Element)nl.item(i);
                game.olderNewPrices.add(Double.valueOf(elementPrice.getTextContent()));
            }
        }

        //USED PRICE
        nl = prices.getElementsByTagName("usedPrice");
        if(nl.getLength() > 0){
            Element usedPrice = (Element)nl.item(0);
            game.usedPrice = Double.valueOf(usedPrice.getTextContent());
        }

        //OLDER USED PRICES
        nl = prices.getElementsByTagName("olderUsedPrices");
        if(nl.getLength() > 0){
            game.olderUsedPrices = new ArrayList();
            Element olderUsedPrices = (Element)nl.item(0);
            nl = olderUsedPrices.getElementsByTagName("price");
            for(int i = 0; i<nl.getLength(); i++){
                Element elementPrice = (Element)nl.item(i);
                game.olderUsedPrices.add(Double.valueOf(elementPrice.getTextContent()));
            }
        }

        //PREORDER PRICE
        nl = prices.getElementsByTagName("preorderPrice");
        if(nl.getLength() > 0){
            Element preorderPrice = (Element)nl.item(0);
            game.preorderPrice = Double.valueOf(preorderPrice.getTextContent());
        }

        //DIGITAL PRICE
        nl = prices.getElementsByTagName("digitalPrice");
        if(nl.getLength() > 0){
            Element digitalPrice = (Element)nl.item(0);
            game.digitalPrice = Double.valueOf(digitalPrice.getTextContent());
        }

        //PEGI
        nl = gameElement.getElementsByTagName("pegi");
        if(nl.getLength() > 0){
            Element pegi = (Element)nl.item(0);
            nl = pegi.getElementsByTagName("type");
            game.pegi = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                Element type = (Element)nl.item(i);
                game.pegi.add(type.getTextContent());
            }
        }

        //GENRES
        nl = gameElement.getElementsByTagName("genres");
        if(nl.getLength() > 0){
            Element genres = (Element)nl.item(0);
            nl = genres.getElementsByTagName("genre");
            game.genres = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                Element genre = (Element)nl.item(i);
                game.genres.add(genre.getTextContent());
            }
        }

        //OFFICIAL SITE
        nl = gameElement.getElementsByTagName("officialSite");
        if(nl.getLength() > 0){
            Element officialSite = (Element)nl.item(0);
            game.officialSite = officialSite.getChildNodes().item(0).getTextContent();
        }

        //PLAYERS
        nl = gameElement.getElementsByTagName("players");
        if(nl.getLength() > 0){
            Element players = (Element)nl.item(0);
            game.players = players.getChildNodes().item(0).getTextContent();
        }

        //RELEASE DATE
        nl = gameElement.getElementsByTagName("releaseDate");
        if(nl.getLength() > 0){
            Element releaseDate = (Element)nl.item(0);
            game.releaseDate = releaseDate.getTextContent();
        }

        //PROMOS
        nl = gameElement.getElementsByTagName("promos");
        if(nl.getLength() > 0){
            Element promos = (Element)nl.item(0);
            nl = promos.getElementsByTagName("promo");
            game.promo = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                Element promo = (Element)nl.item(i);

                String header = promo.getElementsByTagName("header").item(0).getChildNodes().item(0).getTextContent();
                String validity = promo.getElementsByTagName("validity").item(0).getChildNodes().item(0).getTextContent();

                String message = null;
                String messageURL = null;
                if(promo.getElementsByTagName("message").getLength() > 0){
                    message = promo.getElementsByTagName("message").item(0).getChildNodes().item(0).getTextContent();
                    messageURL = promo.getElementsByTagName("messageURL").item(0).getChildNodes().item(0).getTextContent();
                }

                Promo p = new Promo(header, validity, message, messageURL);
                game.promo.add(p);
            }
        }

        //DESCRIPTION
        nl = gameElement.getElementsByTagName("description");
        if(nl.getLength() > 0){
            Element description = (Element)nl.item(0);
            game.description = description.getChildNodes().item(0).getTextContent();
        }

        //VALID FOR PROMO
        nl = gameElement.getElementsByTagName("validForPromo");
        if(nl.getLength() > 0){
            Element validForPromo = (Element)nl.item(0);
            game.validForPromotions = Boolean.valueOf(validForPromo.getTextContent());
        }

        return game;
    }

    public static void validateGame(File f) throws Exception  {
        return;
        /*Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(DirectoryManager.SCHEMA_GAME));
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new StreamSource(f));*/
    }

    // IMPORT/EXPORT METHODS FOR GAMES CLASS ----------------------------------

    public static void exportGames(Games games) throws Exception {

        File f = new File(WISHLIST);
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));

        for ( GamePreview game : games ){
            exportGame((Game)game);
            bw.write( game.getId() + ";" );
        }

        bw.close();
    }

    public static Games importGames() throws Exception {

        Games games = new Games();

        File f = new File(WISHLIST);

        BufferedReader br = new BufferedReader(new FileReader(f));

        String row = br.readLine();
        String[] IDs = row.split(";");

        for( String id : IDs ){
            games.add(importGame(id));
        }

        return games;
    }

    //CHECK FILE EXISTS

    public static boolean wishlistExists(){
        File f = new File(WISHLIST);
        return f.exists();
    }

    //DELETE SYSTEM -----------------------------------------------------------

    public static void deleteTempGames() throws Exception{
        File f = new File(WISHLIST);
        BufferedReader br = new BufferedReader(new FileReader(f));
        String row = br.readLine();
        String[] IDs = row.split(";");

        File wishlistDir = new File(GAMES_DIRECTORY);
        File[] games = wishlistDir.listFiles();

        for(File game : games){
            boolean toDelete = true;
            for(int i=0;i<IDs.length && toDelete;i++){
                if(game.getName().equals(IDs[i]) || game.getName().equals("data.csv")){
                    toDelete=false;
                }
            }

            if(toDelete)
                deleteFolderRecursive(game);
        }
    }

    public static void deleteAllGames() throws Exception{
        File wishlistDir = new File(GAMES_DIRECTORY);
        deleteFolderRecursive(wishlistDir);
    }

    public static void deleteFolderRecursive(File f){
        if(f.isDirectory()){
            File[] files = f.listFiles();
            for (File file : files){
                deleteFolderRecursive(file);
            }
        }

        f.delete();
    }

    //CREATE VALIDATOR --------------------------------------------------------

    public static void createValidatorFile() throws Exception{
        File f = new File(SCHEMA_GAME);

        if(!f.exists()) {

            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

            String schema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "\n" +
                    "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                    "\t\n" +
                    "\t<xs:element name=\"game\">\t\t\n" +
                    "\t\t<xs:complexType>\n" +
                    "\t\t\t<xs:sequence>\n" +
                    "\t\t\t\t<xs:element name=\"title\" type=\"xs:string\"/>\n" +
                    "\t\t\t\t<xs:element name=\"publisher\" type=\"xs:string\"/>\n" +
                    "\t\t\t\t<xs:element name=\"platform\" type=\"xs:string\"/>\n" +
                    "\t\t\t\t<xs:element name=\"prices\">\n" +
                    "\t\t\t\t\t<xs:complexType>\n" +
                    "\t\t\t\t\t\t<xs:sequence>\n" +
                    "\n" +
                    "\t\t\t\t\t\t\t<xs:element name=\"newPrice\" minOccurs=\"0\" type=\"xs:double\"/>\n" +
                    "\n" +
                    "\t\t\t\t\t\t\t<xs:element name=\"olderNewPrices\" minOccurs=\"0\">\n" +
                    "\t\t\t\t\t\t\t\t<xs:complexType>\n" +
                    "\t\t\t\t\t\t\t\t\t<xs:sequence>\n" +
                    "\t\t\t\t\t\t\t\t\t\t<xs:element name=\"price\" type=\"xs:double\" minOccurs=\"1\" maxOccurs=\"unbounded\"/>\n" +
                    "\t\t\t\t\t\t\t\t\t</xs:sequence>\n" +
                    "\t\t\t\t\t\t\t\t</xs:complexType>\n" +
                    "\t\t\t\t\t\t\t</xs:element>\n" +
                    "\n" +
                    "\t\t\t\t\t\t\t<xs:element name=\"usedPrice\" minOccurs=\"0\" type=\"xs:double\"/>\n" +
                    "\n" +
                    "\t\t\t\t\t\t\t<xs:element name=\"olderUsedPrices\" minOccurs=\"0\">\n" +
                    "\t\t\t\t\t\t\t\t<xs:complexType>\n" +
                    "\t\t\t\t\t\t\t\t\t<xs:sequence>\n" +
                    "\t\t\t\t\t\t\t\t\t\t<xs:element name=\"price\" type=\"xs:double\" minOccurs=\"1\" maxOccurs=\"unbounded\"/>\n" +
                    "\t\t\t\t\t\t\t\t\t</xs:sequence>\n" +
                    "\t\t\t\t\t\t\t\t</xs:complexType>\n" +
                    "\t\t\t\t\t\t\t</xs:element>\n" +
                    "\n" +
                    "\t\t\t\t\t\t\t<xs:element name=\"preorderPrice\" minOccurs=\"0\" type=\"xs:double\"/>\n" +
                    "\t\t\t\t\t\t\t<xs:element name=\"digitalPrice\" minOccurs=\"0\" type=\"xs:double\"/>\n" +
                    "                                                        \n" +
                    "\t\t\t\t\t\t</xs:sequence>\n" +
                    "\t\t\t\t\t</xs:complexType>\n" +
                    "\t\t\t\t</xs:element>\n" +
                    "\n" +
                    "\t\t\t\t<xs:element name=\"pegi\" minOccurs=\"0\">\n" +
                    "\t\t\t\t\t<xs:complexType>\n" +
                    "\t\t\t\t\t\t<xs:sequence>\n" +
                    "\t\t\t\t\t\t\t<xs:element name=\"type\" type=\"xs:string\" minOccurs=\"1\" maxOccurs=\"unbounded\"/>\n" +
                    "\t\t\t\t\t\t</xs:sequence>\n" +
                    "\t\t\t\t\t</xs:complexType>\n" +
                    "\t\t\t\t</xs:element>\n" +
                    "\n" +
                    "\t\t\t\t<xs:element name=\"genres\" minOccurs=\"0\">\n" +
                    "\t\t\t\t\t<xs:complexType>\n" +
                    "\t\t\t\t\t\t<xs:sequence>\n" +
                    "\t\t\t\t\t\t\t<xs:element name=\"genre\" type=\"xs:string\" minOccurs=\"1\" maxOccurs=\"unbounded\"/>\n" +
                    "\t\t\t\t\t\t</xs:sequence>\n" +
                    "\t\t\t\t\t</xs:complexType>\n" +
                    "\t\t\t\t</xs:element>\n" +
                    "\n" +
                    "\t\t\t\t<xs:element name=\"officialSite\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
                    "\t\t\t\t<xs:element name=\"players\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
                    "\t\t\t\t<xs:element name=\"releaseDate\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
                    "\t\t\t\t\n" +
                    "\t\t\t\t<xs:element name=\"promos\" minOccurs=\"0\">\n" +
                    "\t\t\t\t\t<xs:complexType>\n" +
                    "\t\t\t\t\t\t<xs:sequence>\n" +
                    "\t\t\t\t\t\t\t<xs:element name=\"promo\" minOccurs=\"1\" maxOccurs=\"unbounded\">\n" +
                    "\t\t\t\t\t\t\t\t<xs:complexType>\n" +
                    "\t\t\t\t\t\t\t\t\t<xs:sequence>\n" +
                    "\t\t\t\t\t\t\t\t\t\t<xs:element name=\"header\" type=\"xs:string\" minOccurs=\"1\"/>\n" +
                    "\t\t\t\t\t\t\t\t\t\t<xs:element name=\"validity\" type=\"xs:string\" minOccurs=\"1\"/>\n" +
                    "                                        <xs:element name=\"message\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
                    "                                        <xs:element name=\"messageURL\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
                    "\t\t\t\t\t\t\t\t\t</xs:sequence>\n" +
                    "\t\t\t\t\t\t\t\t</xs:complexType>\n" +
                    "\t\t\t\t\t\t\t</xs:element>\n" +
                    "\t\t\t\t\t\t</xs:sequence>\n" +
                    "\t\t\t\t\t</xs:complexType>\n" +
                    "\t\t\t\t</xs:element>\n" +
                    "\n" +
                    "\t\t\t\t<xs:element name=\"description\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
                    "\t\t\t\t<xs:element name=\"validForPromo\" type=\"xs:boolean\" minOccurs=\"0\"/>\n" +
                    "\n" +
                    "\t\t\t</xs:sequence>\n" +
                    "\t\t\t<xs:attribute name=\"id\" type=\"xs:string\"/>                      \n" +
                    "\t\t</xs:complexType>\n" +
                    "\t</xs:element>\n" +
                    "</xs:schema>";

            bw.write(schema);
            bw.close();
        }
    }
}