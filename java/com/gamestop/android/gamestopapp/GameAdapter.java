package com.gamestop.android.gamestopapp;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.text.method.Touch;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends ArrayAdapter<GamePreview>{
    private List<GamePreview> list;
    MainActivity main;
    private TextView title, platform, newPrice, publisher, oldNewPrice, usedPrice, oldUsedPrice;
    private ImageView image;

    public GameAdapter(List<GamePreview> list, MainActivity main) {
        super(main, R.layout.gamepreview_compact, list);
        this.list = list;
        this.main=main;
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
        image = (ImageView) convertView.findViewById(R.id.image);

        GamePreview game = getItem(position);

        title.setText(game.getTitle());
        platform.setText(game.getPlatform());
        publisher.setText(game.getPublisher());

        if(!String.valueOf(game.getNewPrice()).equals("null"))
            newPrice.setText(String.valueOf(game.getNewPrice()) + "€");
        else
            newPrice.setText("NO INFO");

        if(!String.valueOf(game.getUsedPrice()).equals("null"))
            usedPrice.setText(String.valueOf(game.getUsedPrice()) + "€");
        else
            usedPrice.setText("NO INFO");

        image.setImageURI(Uri.fromFile(new File(game.getCover(main))));

        oldNewPrice.setPaintFlags(usedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        oldUsedPrice.setPaintFlags(usedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        return convertView;
    }
}
