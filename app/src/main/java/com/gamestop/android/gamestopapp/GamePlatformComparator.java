package com.gamestop.android.gamestopapp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Comparator;

/**
 *
 * @author android
 */
public class GamePlatformComparator implements Comparator<GamePreview> {

    @Override
    public int compare(GamePreview game1, GamePreview game2) {
        return game1.getPlatform().compareToIgnoreCase(game2.getPlatform());
    }

}

