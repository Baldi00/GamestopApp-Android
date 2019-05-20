package com.gamestop.android.gamestopapp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ActivitySettings extends AppCompatActivity {

    private SettingsManager settingsManager;

    private boolean notificationServiceEnabled;
    private int notificationServiceSleepTime;
    private boolean updateOnStartEnabled;
    private boolean bunnyEnabled;
    private boolean notificationSoundEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSettingsGraphic();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyChanges(null);
    }

    public void setSettingsGraphic(){
        try {
            settingsManager = SettingsManager.getInstance();
            notificationServiceEnabled = settingsManager.isNotificationServiceEnabled();
            notificationServiceSleepTime = settingsManager.getNotificationServiceSleepTime();
            updateOnStartEnabled = settingsManager.isUpdateOnStartEnabled();
            bunnyEnabled = settingsManager.isBunnyEnabled();
            notificationSoundEnabled = settingsManager.isNotificationSoundEnabled();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((Switch)findViewById(R.id.notificationServiceEnabled)).setChecked(notificationServiceEnabled);

        Spinner spinner = (Spinner)findViewById(R.id.notificationServiceSleepTime);
        switch (notificationServiceSleepTime){
            case 600000:
                spinner.setSelection(0);
                break;
            case 1800000:
                spinner.setSelection(1);
                break;
            case 3600000:
                spinner.setSelection(2);
                break;
            case 10800000:
                spinner.setSelection(3);
                break;
            case 21600000:
                spinner.setSelection(4);
                break;
            case 43200000:
                spinner.setSelection(5);
                break;
            case 5000:
                spinner.setSelection(6);
                break;
        }

        ((Switch)findViewById(R.id.updateOnStartEnabled)).setChecked(updateOnStartEnabled);

        ((Switch)findViewById(R.id.bunnyEnabled)).setChecked(bunnyEnabled);

        ((Switch)findViewById(R.id.notificationSoundEnabled)).setChecked(notificationSoundEnabled);
    }

    public void deleteTempGames(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("Sei sicuro di voler cancellare i file temporanei?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            DirectoryManager.deleteTempGames(ActivityMain.getWishlistData());
                            ActivityMain.resetResearh();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"File temporanei cancellati",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        dialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    public void deleteAllGames(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("Tutti i giochi verranno cancellati! Sei sicuro di voler svuotare la wishlist?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            DirectoryManager.deleteAllGames();
                            ActivityMain.resetAll();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"Wishlist svuotata",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        dialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    public void resetSettings(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("Vuoi ripristinare le impostazioni di default?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            settingsManager.resetSettings();
                            setSettingsGraphic();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        dialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    public void resetAll(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("L'app verrà ripristinata e tutti i giochi verranno cancellati! Sei sicuro di voler ritornare allo stato iniziale? (L'app verrà chiusa)");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            DirectoryManager.deleteAllGames();
                            DirectoryManager.deleteFilesRecursive(new File(DirectoryManager.getAppDir() + "config.txt"));
                            DirectoryManager.deleteFilesRecursive(new File(DirectoryManager.getAppDir() + "Game.xsd"));
                            DirectoryManager.deleteFilesRecursive(new File(DirectoryManager.getAppDir() + "notificationId.txt"));

                            NotificationManager nManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                            nManager.cancelAll();

                            finishAndRemoveTask();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        dialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    public void applyChanges(View v){

        settingsManager.setNotificationServiceEnabled(((Switch)findViewById(R.id.notificationServiceEnabled)).isChecked());

        switch (((Spinner)findViewById(R.id.notificationServiceSleepTime)).getSelectedItemPosition()){
            case 0:
                settingsManager.setNotificationServiceSleepTime(600000);
                break;
            case 1:
                settingsManager.setNotificationServiceSleepTime(1800000);
                break;
            case 2:
                settingsManager.setNotificationServiceSleepTime(3600000);
                break;
            case 3:
                settingsManager.setNotificationServiceSleepTime(10800000);
                break;
            case 4:
                settingsManager.setNotificationServiceSleepTime(21600000);
                break;
            case 5:
                settingsManager.setNotificationServiceSleepTime(43200000);
                break;
            case 6:
                settingsManager.setNotificationServiceSleepTime(5000);
                break;
        }

        settingsManager.setUpdateOnStartEnabled(((Switch)findViewById(R.id.updateOnStartEnabled)).isChecked());
        settingsManager.setBunnyEnabled(((Switch)findViewById(R.id.bunnyEnabled)).isChecked());
        settingsManager.setNotificationSoundEnabled(((Switch)findViewById(R.id.notificationSoundEnabled)).isChecked());

        try {
            settingsManager.saveSettings();
            Toast.makeText(this,"Modifiche salvate",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Errore durante il salvataggio delle impostazioni",Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
