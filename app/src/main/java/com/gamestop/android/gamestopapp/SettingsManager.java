package com.gamestop.android.gamestopapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsManager {

    private static SettingsManager instance;
    private static String settingsFilePath = DirectoryManager.getAppDir() + "config.txt";

    private boolean notificationServiceEnabled;     // notification service enabled
    private int notificationServiceSleepTime;       // notification service sleep time
    private boolean notificationSoundEnabled;       // notification sound
    private boolean updateOnStartEnabled;           // update games on startup
    private boolean bunnyEnabled;                   // visualize bunny

    private SettingsManager() throws IOException {

        File f = new File(settingsFilePath);

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
        br.close();
    }

    public static SettingsManager getInstance() throws IOException {
        if(instance == null) {
            instance = new SettingsManager();
        }

        return instance;
    }

    private void createSettingsFile() throws IOException {

        notificationServiceEnabled = true;
        notificationServiceSleepTime = 3600000;
        notificationSoundEnabled = false;
        updateOnStartEnabled = true;
        bunnyEnabled = false;

        this.saveSettings();
    }

    /**
     * Save changed settings
     * @throws IOException
     */
    public void saveSettings() throws IOException {

        File f = new File(settingsFilePath);
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));

        bw.write( Boolean.toString(notificationServiceEnabled) );   bw.newLine();
        bw.write( Integer.toString(notificationServiceSleepTime) ); bw.newLine();
        bw.write( Boolean.toString(notificationSoundEnabled) );     bw.newLine();
        bw.write( Boolean.toString(updateOnStartEnabled) );         bw.newLine();
        bw.write( Boolean.toString(bunnyEnabled) );                 bw.newLine();
        bw.close();
    }

    /**
     * Set the default default settings
     * @throws IOException
     */
    public void resetSettings() throws IOException {
        this.createSettingsFile();
    }

    // GETTER & SETTER -----------------------------------------------------------

    /**
     * Returns true if the notification service is enabled, otherwise false
     * @return true if enabled, otherwise false
     */
    public boolean isNotificationServiceEnabled() {
        return notificationServiceEnabled;
    }

    /**
     * Set true to enabled the service, false otherwise
     * @param notificationServiceEnabled true to enabled the service, false otherwise
     */
    public void setNotificationServiceEnabled(boolean notificationServiceEnabled) {
        this.notificationServiceEnabled = notificationServiceEnabled;
    }

    /**
     * Returns the sleep time in milliseconds of the notification service
     * @return sleep time in milliseconds
     */
    public int getNotificationServiceSleepTime() {
        return notificationServiceSleepTime;
    }

    /**
     * Set the sleep time of the notification service
     * @param notificationServiceSleepTime sleep time in milliseconds
     */
    public void setNotificationServiceSleepTime(int notificationServiceSleepTime)  {
        this.notificationServiceSleepTime = notificationServiceSleepTime;
    }

    /**
     * Returns true if the notification sound is enabled, otherwise false
     * @return true if enabled, otherwise false
     */
    public boolean isNotificationSoundEnabled() {
        return notificationSoundEnabled;
    }

    /**
     * Set true to enable notification sound, false otherwise
     * @param notificationSoundEnabled true to enable notification sound, false otherwise
     */
    public void setNotificationSoundEnabled(boolean notificationSoundEnabled) {
        this.notificationSoundEnabled = notificationSoundEnabled;
    }

    /**
     * Returns true if games are updated on app startup, false otherwise
     * @return true if games are updated on app startup, false otherwise
     */
    public boolean isUpdateOnStartEnabled() {
        return updateOnStartEnabled;
    }

    /**
     * Set true to enable games update on app startup, false otherwise
     * @param updateOnStartEnabled true to enable games update on app startup, false otherwise
     */
    public void setUpdateOnStartEnabled(boolean updateOnStartEnabled) {
        this.updateOnStartEnabled = updateOnStartEnabled;
    }

    /**
     * Returns true if the bunny is visible, false otherwise
     * @return true if the bunny is visible, false otherwise
     */
    public boolean isBunnyEnabled() {
        return bunnyEnabled;
    }

    /**
     * Set true to make the bunny visible, false otherwise
     * @param bunnyEnabled true to make the bunny visible, false otherwise
     */
    public void setBunnyEnabled(boolean bunnyEnabled) {
        this.bunnyEnabled = bunnyEnabled;
    }
}
