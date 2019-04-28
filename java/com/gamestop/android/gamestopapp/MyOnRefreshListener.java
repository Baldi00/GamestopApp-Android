package com.gamestop.android.gamestopapp;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout pullToRefresh;
    private ActivityMain main;

    public MyOnRefreshListener(SwipeRefreshLayout pullToRefresh, ActivityMain main) {
        this.pullToRefresh = pullToRefresh;
        this.main = main;
    }

    //Call the update for the games in wishlist
    @Override
    public void onRefresh() {
        /*Updater up = new Updater(main,this);
        up.execute();*/

        pullToRefresh.setRefreshing(false);
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
            for(GamePreview gp : main.getWishlistData()){

                Document html = null;
                try {
                    html = Jsoup.connect(gp.getURL()).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Element body = html.body();

                updatePrices(body, gp);
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
