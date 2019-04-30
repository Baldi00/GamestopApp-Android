package com.gamestop.android.gamestopapp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ActivitySettings extends AppCompatActivity {

    private boolean toApplyChanges = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        File f = new File(DirectoryManager.getAppDir()+"config.txt");

        if(f.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                boolean backgroundServiceEnabled = Boolean.parseBoolean(br.readLine());
                int millisecondsToSleep = Integer.parseInt(br.readLine());
                boolean updateOnAppStart = Boolean.parseBoolean(br.readLine());
                boolean visualizeGamestopBunny = Boolean.parseBoolean(br.readLine());
                boolean notificationSound = Boolean.parseBoolean(br.readLine());

                if(!backgroundServiceEnabled){
                    ((Switch)findViewById(R.id.backgroundService)).setChecked(false);
                }

                Spinner spinner = (Spinner)findViewById(R.id.updateInterval);
                switch (millisecondsToSleep){
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

                if(!updateOnAppStart){
                    ((Switch)findViewById(R.id.updateOnAppStart)).setChecked(false);
                }

                if(visualizeGamestopBunny){
                    ((Switch)findViewById(R.id.visualizeGamestopBunny)).setChecked(true);
                }

                if(notificationSound){
                    ((Switch)findViewById(R.id.notificationSound)).setChecked(true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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
                            DirectoryManager.deleteTempGames();
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
                            DirectoryManager.deleteFolderRecursive(new File(DirectoryManager.getAppDir() + "config.txt"));
                            DirectoryManager.deleteFolderRecursive(new File(DirectoryManager.getAppDir() + "Game.xsd"));
                            DirectoryManager.deleteFolderRecursive(new File(DirectoryManager.getAppDir() + "notificationId.txt"));

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
        File f = new File(DirectoryManager.getAppDir()+"config.txt");

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

            bw.write(String.valueOf(((Switch)findViewById(R.id.backgroundService)).isChecked())); //Notification service enabled (background)
            bw.newLine();

            /*switch (((Spinner)findViewById(R.id.updateInterval)).getSelectedItemPosition()){
                case 0:
                    bw.write("600000");
                    break;
                case 1:
                    bw.write("1800000");
                    break;
                case 2:
                    bw.write("3600000");
                    break;
                case 3:
                    bw.write("10800000");
                    break;
                case 4:
                    bw.write("5000");
                    break;
            }*/
            bw.write("10000"); //TODO TEST
            bw.newLine();

            bw.write(String.valueOf(((Switch)findViewById(R.id.updateOnAppStart)).isChecked())); //Update games on app start
            bw.newLine();

            bw.write(String.valueOf(((Switch)findViewById(R.id.visualizeGamestopBunny)).isChecked())); //Update games on app start
            bw.newLine();

            bw.write(String.valueOf(((Switch)findViewById(R.id.notificationSound)).isChecked())); //Notification sound
            bw.newLine();

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this,"Modifiche salvate",Toast.LENGTH_SHORT).show();
        finish();
    }

    //TODO TEST
    public void debugNotifications(View v){
        try {
            Games games = DirectoryManager.importGames();
            GamePreview g = games.get(0);
            games.clear();
            g.debugNotifications();
            games.add(g);
            DirectoryManager.exportGames(games);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
