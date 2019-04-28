package com.gamestop.android.gamestopapp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Utente
 */
public class GameException extends RuntimeException {

    @Override
    public String toString() {
        return "failed to create a Game";
    }

}

