package com.gamestop.android.gamestopapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout pullToRefresh;
    private ActivityMain main;
    private int notificationId;
    private NotificationManager notificationManager;

    public MyOnRefreshListener(SwipeRefreshLayout pullToRefresh, ActivityMain main) {
        this.pullToRefresh = pullToRefresh;
        this.main = main;
    }

    //Call the update for the games in wishlist
    @Override
    public void onRefresh() {
        Updater up = new Updater(main,this);
        up.execute();

        //pullToRefresh.setRefreshing(false);
    }

    private void onEndRefresh(){
        pullToRefresh.setRefreshing(false);
        main.getWishlistAdapter().notifyDataSetChanged();
        Toast.makeText(main,"Aggiornamento terminato correttamente",Toast.LENGTH_SHORT).show();
    }

    private class Updater extends AsyncTask{
        private ActivityMain main;
        MyOnRefreshListener refreshListener;

        public Updater(ActivityMain main, MyOnRefreshListener refreshListener){
            this.main = main;
            this.refreshListener = refreshListener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {


            //TODO TEST
            //Without this update is too fast
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            ConnectivityManager cm = (ConnectivityManager)main.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

            if(isConnected) {
                if (DirectoryManager.wishlistExists()) {
                    Games gs = null;

                    try {
                        gs = DirectoryManager.importGames();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(gs!=null) {
                        for (GamePreview gp : gs) {
                            Game game = (Game) gp;
                            //List<String> notifications = game.update();

                            //TODO
                            List<String> notifications = new ArrayList<String>();
                            notifications.add("Gioco in sconto");

                            if (notifications != null) {

                                for (String str : notifications) {


                                    //Read notification id
                                    try {
                                        BufferedReader br = new BufferedReader(new FileReader(DirectoryManager.getAppDir()+"notificationId.txt"));
                                        notificationId = Integer.parseInt(br.readLine());
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Notification.Builder notification = new Notification.Builder(main)
                                            .setSmallIcon(R.drawable.notification_icon)
                                            .setContentTitle(game.getTitle())
                                            .setContentText(str);

                                    Intent intent = new Intent(main, ActivityMain.class);
                                    intent.putExtra("from","notification");
                                    PendingIntent pendingIntent = PendingIntent.getActivity(main, 0, intent, 0);
                                    notification.setContentIntent(pendingIntent);

                                    notificationManager = (NotificationManager) main.getSystemService(main.NOTIFICATION_SERVICE);
                                    notificationManager.notify(notificationId, notification.build());

                                    notificationId++;

                                    //Write notification id
                                    try {
                                        BufferedWriter bw = new BufferedWriter(new FileWriter(DirectoryManager.getAppDir()+"notificationId.txt"));
                                        bw.write(""+notificationId);
                                        bw.newLine();
                                        bw.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            refreshListener.onEndRefresh();
        }

        /**
         * returned value checked
         * @param buySection
         * @return
         */
        private boolean updatePrices(Element buySection, GamePreview g) {

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
            Double newPriceCopy = g.newPrice;
            Double usedPriceCopy = g.usedPrice;
            Double preorderPriceCopy = g.preorderPrice;

            // if the prices are removed they don't change
            // example: newPrice is 20€ > then newPrice no longer exist > newPrice is still 20€
            g.newPrice = null;
            g.usedPrice = null;
            g.preorderPrice = null;
            g.olderNewPrices = null;
            g.olderUsedPrices = null;

            for (Element singleVariantDetails : buySection.getElementsByClass("singleVariantDetails")) {

                if (singleVariantDetails.getElementsByClass("singleVariantText").isEmpty()) {
                    throw new GameException();
                }

                Element singleVariantText = singleVariantDetails.getElementsByClass("singleVariantText").get(0);

                if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Nuovo")) {
                    String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();

                    g.newPrice = stringToPrice(price);

                    for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {

                        if ( g.olderNewPrices == null ){
                            g.olderNewPrices = new ArrayList<>();
                        }

                        price = olderPrice.text();
                        g.olderNewPrices.add(stringToPrice(price));
                    }
                }

                if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Usato")) {
                    String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();

                    g.usedPrice = stringToPrice(price);

                    for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {

                        if ( g.olderUsedPrices == null ){
                            g.olderUsedPrices = new ArrayList<>();
                        }

                        price = olderPrice.text();
                        g.olderUsedPrices.add(stringToPrice(price));
                    }
                }

                if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Prenotazione")) {
                    String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();

                    g.preorderPrice = stringToPrice(price);
                }
            }

            if ( g.newPrice != null && !g.newPrice.equals(newPriceCopy) )
                changes = true;

            if ( g.usedPrice != null && !g.usedPrice.equals(usedPriceCopy) )
                changes = true;

            if ( g.preorderPrice != null && !g.preorderPrice.equals(preorderPriceCopy) )
                changes = true;

            return changes;
        }

        protected double stringToPrice(String price) {

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
    }
}
