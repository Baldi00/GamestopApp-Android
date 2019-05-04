package com.gamestop.android.gamestopapp;

import android.widget.Spinner;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {

    private static Settings instance;
    private static String configFileName = DirectoryManager.getAppDir() + "config.txt";

    private boolean notificationServiceEnabled;
    private int notificationServiceSleepTime;
    private boolean notificationSoundEnabled;
    private boolean updateOnStartEnabled;
    private boolean visualizeBunnyEnabled;

    private Settings() throws IOException {

        File f = new File(configFileName);

        // create file if doesn't exist
        if ( !f.exists() ){
            this.createFile();
        }

        // read parameters
        BufferedReader br = new BufferedReader(new FileReader(f));

        notificationServiceEnabled = Boolean.parseBoolean(br.readLine());
        notificationServiceSleepTime = Integer.parseInt(br.readLine());
        updateOnStartEnabled = Boolean.parseBoolean(br.readLine());
        visualizeBunnyEnabled = Boolean.parseBoolean(br.readLine());
        notificationSoundEnabled = Boolean.parseBoolean(br.readLine());
    }

    public static Settings getInstance() throws IOException {
        if(instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    private void createFile() throws IOException {
        File f = new File(configFileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));

        // Notification service enabled (background)
        bw.write("true");   bw.newLine();

        // Notification service sleep time (600000ms = 10min)
        bw.write("600000"); bw.newLine();

        // Update games on app start
        bw.write("true");   bw.newLine();

        // Visualize gamestop bunny
        bw.write("false");  bw.newLine();

        // Notification sound
        bw.write("false");  bw.newLine();

        bw.close();
    }

    // getters and setters
    // TODO : save file after set

    public static String getConfigFileName() {
        return configFileName;
    }

    public static void setConfigFileName(String configFileName) {
        Settings.configFileName = configFileName;
    }

    public boolean isNotificationServiceEnabled() {
        return notificationServiceEnabled;
    }

    public void setNotificationServiceEnabled(boolean notificationServiceEnabled) {
        this.notificationServiceEnabled = notificationServiceEnabled;
    }

    public int getNotificationServiceSleepTime() {
        return notificationServiceSleepTime;
    }

    public void setNotificationServiceSleepTime(int notificationServiceSleepTime) {
        this.notificationServiceSleepTime = notificationServiceSleepTime;
    }

    public boolean isNotificationSoundEnabled() {
        return notificationSoundEnabled;
    }

    public void setNotificationSoundEnabled(boolean notificationSoundEnabled) {
        this.notificationSoundEnabled = notificationSoundEnabled;
    }

    public boolean isUpdateOnStartEnabled() {
        return updateOnStartEnabled;
    }

    public void setUpdateOnStartEnabled(boolean updateOnStartEnabled) {
        this.updateOnStartEnabled = updateOnStartEnabled;
    }

    public boolean isVisualizeBunnyEnabled() {
        return visualizeBunnyEnabled;
    }

    public void setVisualizeBunnyEnabled(boolean visualizeBunnyEnabled) {
        this.visualizeBunnyEnabled = visualizeBunnyEnabled;
    }
}
