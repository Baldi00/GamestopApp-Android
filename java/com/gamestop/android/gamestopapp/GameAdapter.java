package com.gamestop.android.gamestopapp;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;

public class GameAdapter extends ArrayAdapter<GamePreview>{
    private TextView title, platform, publisher, newPrice, oldNewPrice, usedPrice, oldUsedPrice, digitalPrice, preorderPrice;
    private ImageView image;

    public GameAdapter(Games list, ActivityMain main) {
        super(main, R.layout.gamepreview_compact, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.gamepreview_compact, null);
        title = (TextView)convertView.findViewById(R.id.title);
        platform = (TextView)convertView.findViewById(R.id.platform);
        publisher = (TextView)convertView.findViewById(R.id.publisher);
        newPrice = (TextView)convertView.findViewById(R.id.newPrice);
        usedPrice = (TextView)convertView.findViewById(R.id.usedPrice);
        oldNewPrice = (TextView)convertView.findViewById(R.id.oldNewPrice);
        oldUsedPrice = (TextView)convertView.findViewById(R.id.oldUsedPrice);
        digitalPrice = (TextView)convertView.findViewById(R.id.digitalPrice);
        preorderPrice = (TextView)convertView.findViewById(R.id.preorderPrice);
        image = (ImageView) convertView.findViewById(R.id.image);

        GamePreview game = getItem(position);

        title.setText(game.getTitle());
        platform.setText(game.getPlatform());
        publisher.setText(game.getPublisher());


        DecimalFormat df = new DecimalFormat("#.00");

        if(!String.valueOf(game.getPreorderPrice()).equals("null")){
            preorderPrice.setText(String.valueOf(df.format(game.getPreorderPrice())) + "€");
            convertView.findViewById(R.id.pricePreorder).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.priceNew).setVisibility(View.GONE);
            convertView.findViewById(R.id.priceUsed).setVisibility(View.GONE);
        } else if(!String.valueOf(game.getDigitalPrice()).equals("null")){
            digitalPrice.setText(String.valueOf(df.format(game.getDigitalPrice())) + "€");
            convertView.findViewById(R.id.priceDigital).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.priceNew).setVisibility(View.GONE);
            convertView.findViewById(R.id.priceUsed).setVisibility(View.GONE);
        } else {
            if (!String.valueOf(game.getNewPrice()).equals("null"))
                newPrice.setText(String.valueOf(df.format(game.getNewPrice())) + "€");
            else
                convertView.findViewById(R.id.priceNew).setVisibility(View.GONE);

            if (!String.valueOf(game.getUsedPrice()).equals("null"))
                usedPrice.setText(String.valueOf(df.format(game.getUsedPrice())) + "€");
            else
                convertView.findViewById(R.id.priceUsed).setVisibility(View.GONE);
        }

        if(game.getOlderNewPrices()!=null && game.getOlderNewPrices().size()>0){
            oldNewPrice.setText(String.valueOf(df.format(game.getOlderNewPrices().get(0))) + "€");
            for(int i=1;i<game.getOlderNewPrices().size();i++){
                oldNewPrice.setText(oldNewPrice.getText().toString() + ", " + String.valueOf(df.format(game.getOlderNewPrices().get(i))) + "€");
            }
        }

        if(game.getOlderUsedPrices()!=null && game.getOlderUsedPrices().size()>0){
            oldUsedPrice.setText(String.valueOf(df.format(game.getOlderUsedPrices().get(0))) + "€");
            for(int i=1;i<game.getOlderUsedPrices().size();i++){
                oldUsedPrice.setText(oldUsedPrice.getText().toString() + ", " + String.valueOf(df.format(game.getOlderUsedPrices().get(i))) + "€");
            }
        }

        image.setImageURI(Uri.fromFile(new File(game.getCover())));

        oldNewPrice.setPaintFlags(usedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        oldUsedPrice.setPaintFlags(usedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        return convertView;
    }
}
