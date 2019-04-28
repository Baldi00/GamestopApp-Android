package com.gamestop.android.gamestopapp;

import android.content.Context;
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

public class GameAdapter extends ArrayAdapter<Game>{
    private Games wishlist;
    Context mContext;
    private TextView title, platform, newPrice, publisher, oldNewPrice, usedPrice, oldUsedPrice;
    private CheckBox checkBox;
    private ImageView image;

    private boolean selecting;

    public GameAdapter(Games wishlist, Context context) {
        super(context, R.layout.gamepreview_compact, wishlist);
        this.wishlist = wishlist;
        this.mContext=context;
        selecting = false;
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

        if(selecting){
            checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
            checkBox.setSelected(false);
            checkBox.setVisibility(View.VISIBLE);
        }else{
            checkBox = convertView.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.GONE);
        }

        Game game = getItem(position);

        title.setText(game.getTitle());
        platform.setText(game.getPlatform());
        publisher.setText(game.getPublisher());
        newPrice.setText(String.valueOf(game.getNewPrice()) + "€");
        usedPrice.setText(String.valueOf(game.getUsedPrice()) + "€");
        final Context context = image.getContext();
        int id = context.getResources().getIdentifier(game.getCover().substring(0,game.getCover().lastIndexOf(".")), "drawable", context.getPackageName());
        image.setImageResource(id);

        return convertView;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
        this.notifyDataSetChanged();
    }

    public boolean isSelecting() {
        return selecting;
    }
}
