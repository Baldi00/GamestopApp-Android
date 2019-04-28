package com.gamestop.android.gamestopapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ActivitySettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void deleteTempDir(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("Sei sicuro di voler cancellare i file temporanei?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFolderRecursive(new File(DirectoryManager.getTempDir(getApplicationContext())));
                        dialog.cancel();
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

    public void deleteAll(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogActivityGamePage);
        dialog.setMessage("Tutti i giochi verranno cancellati! Sei sicuro di voler resettare l'applicazione?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFolderRecursive(new File(DirectoryManager.getTempDir(getApplicationContext())));
                        deleteFolderRecursive(new File(DirectoryManager.getWishlistDir(getApplicationContext())));
                        dialog.cancel();
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

    //Delete a folder with its content
    public static void deleteFolderRecursive(File f){
        if(f.isDirectory()){
            File[] files = f.listFiles();
            for (File file : files){
                deleteFolderRecursive(file);
            }
        }

        f.delete();
    }
}
