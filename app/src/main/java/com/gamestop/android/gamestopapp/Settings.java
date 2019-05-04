package com.gamestop.android.gamestopapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {

    private static Settings instance;
    private static String settingsFileName = DirectoryManager.getAppDir() + "config.txt";

    private boolean notificationServiceEnabled;     // notification service enabled
    private int notificationServiceSleepTime;       // notification service sleep time
    private boolean notificationSoundEnabled;       // notification sound
    private boolean updateOnStartEnabled;           // update games on startup
    private boolean bunnyEnabled;                   // visualize bunny

    private Settings() throws IOException {

        File f = new File(settingsFileName);

        // create file if doesn't exist
        if ( !f.exists() ){
            this.createSettingsFile();
        }

        // read parameters
        BufferedReader br = new BufferedReader(new FileReader(f));

        notificationServiceEnabled = Boolean.parseBoolean(br.readLine());
        notificationServiceSleepTime = Integer.parseInt(br.readLine());
        notificationSoundEnabled = Boolean.parseBoolean(br.readLine());
        updateOnStartEnabled = Boolean.parseBoolean(br.readLine());
        bunnyEnabled = Boolean.parseBoolean(br.readLine());
    }

    public static Settings getInstance() throws IOException {
        if(instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    private void createSettingsFile() throws IOException {

        notificationServiceEnabled = true;
        notificationServiceSleepTime = 600000;
        notificationSoundEnabled = false;
        updateOnStartEnabled = true;
        bunnyEnabled = false;

        this.saveSettings();
    }

    public void saveSettings() throws IOException {

        File f = new File(settingsFileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));

        bw.write( Boolean.toString(notificationServiceEnabled) );   bw.newLine();
        bw.write( Integer.toString(notificationServiceSleepTime) ); bw.newLine();
        bw.write( Boolean.toString(notificationSoundEnabled) );     bw.newLine();
        bw.write( Boolean.toString(updateOnStartEnabled) );         bw.newLine();
        bw.write( Boolean.toString(bunnyEnabled) );                 bw.newLine();
        bw.close();
    }

    public void resetSettings() throws IOException {
        this.createSettingsFile();
    }

    // getters and setters

    public boolean isNotificationServiceEnabled() {
        return notificationServiceEnabled;
    }

    public void setNotificationServiceEnabled(boolean notificationServiceEnabled) {
        this.notificationServiceEnabled = notificationServiceEnabled;
    }

    public int getNotificationServiceSleepTime() {
        return notificationServiceSleepTime;
    }

    public void setNotificationServiceSleepTime(int notificationServiceSleepTime)  {
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

    public boolean isBunnyEnabled() {
        return bunnyEnabled;
    }

    public void setBunnyEnabled(boolean bunnyEnabled) {
        this.bunnyEnabled = bunnyEnabled;
    }
}
