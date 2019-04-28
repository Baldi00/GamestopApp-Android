package com.gamestop.android.gamestopapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class GameAdapter extends ArrayAdapter<Game>{
    private Games wishlist;
    Context mContext;
    private TextView title, platform, newPrice, publisher, oldNewPrice, usedPrice, oldUsedPrice;
    private ImageView image;

    public GameAdapter(Games wishlist, Context context) {
        super(context, R.layout.gamepreview_compact, wishlist);
        this.wishlist = wishlist;
        this.mContext=context;
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
        image = (ImageView) convertView.findViewById(R.id.image);
        Game game = getItem(position);
        title.setText(game.getTitle());
        platform.setText(game.getPlatform());
        publisher.setText(game.getPublisher());
        newPrice.setText(String.valueOf(game.getNewPrice()) + "€");
        usedPrice.setText(String.valueOf(game.getUsedPrice()) + "€");
        Context context = image.getContext();
        int id = context.getResources().getIdentifier(game.getCover().substring(0,game.getCover().lastIndexOf(".")), "drawable", context.getPackageName());
        image.setImageResource(id);
        return convertView;
    }


}
