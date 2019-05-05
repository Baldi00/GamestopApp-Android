package com.gamestop.android.gamestopapp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ActivitySettings extends AppCompatActivity {

    private boolean toApplyChanges = true;
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

        if(!notificationServiceEnabled){
            ((Switch)findViewById(R.id.notificationServiceEnabled)).setChecked(false);
        }

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
            case 5000:
                spinner.setSelection(4);
                break;
        }

        if(!updateOnStartEnabled){
            ((Switch)findViewById(R.id.updateOnStartEnabled)).setChecked(false);
        }

        if(bunnyEnabled){
            ((Switch)findViewById(R.id.bunnyEnabled)).setChecked(true);
        }

        if(notificationSoundEnabled){
            ((Switch)findViewById(R.id.notificationSoundEnabled)).setChecked(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(toApplyChanges)
            applyChanges(null);
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
                            toApplyChanges = false;
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
                            toApplyChanges = false;
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

    public void resetAll(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("L'app verrà ripristinata e tutti i giochi verranno cancellati! Sei sicuro di voler ritornare allo stato iniziale?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            toApplyChanges = false;
                            DirectoryManager.deleteAllGames();
                            DirectoryManager.deleteFilesRecursive(new File(DirectoryManager.getAppDir() + "config.txt"));
                            DirectoryManager.deleteFilesRecursive(new File(DirectoryManager.getAppDir() + "Game.xsd"));
                            DirectoryManager.deleteFilesRecursive(new File(DirectoryManager.getAppDir() + "notificationId.txt"));

                            NotificationManager nManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                            nManager.cancelAll();

                            Intent i = getBaseContext().getPackageManager().
                                    getLaunchIntentForPackage(getBaseContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(i);

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
