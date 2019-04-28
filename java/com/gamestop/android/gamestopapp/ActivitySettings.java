package com.gamestop.android.gamestopapp;

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

        File version = new File(getFilesDir(),"version.txt");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(version));
            if(Integer.parseInt(bufferedReader.readLine())==1){
                ((CheckBox)findViewById(R.id.checkboxVersion)).setChecked(false);
            } else {
                ((CheckBox)findViewById(R.id.checkboxVersion)).setChecked(true);
            }
        }catch (Exception e){
            Toast.makeText(this,"Impossibile leggere file di configurazione",Toast.LENGTH_LONG).show();
            ((CheckBox)findViewById(R.id.checkboxVersion)).setChecked(true);
        }
    }

    public void changeVersion(View v){
        File f = new File(getFilesDir(),"version.txt");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
            CheckBox c = (CheckBox)v;
            if(c.isChecked()){
                bufferedWriter.write("2");
            } else {
                bufferedWriter.write("1");
            }
            bufferedWriter.close();
        }catch (Exception e){
            Toast.makeText(this, "Impossibile scrivere il file di configurazione",Toast.LENGTH_LONG).show();
        }
    }
}
