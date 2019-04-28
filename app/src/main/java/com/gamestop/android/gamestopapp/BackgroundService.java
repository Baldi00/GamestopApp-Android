package com.gamestop.android.gamestopapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends Service {
    private int notificationId;
    private NotificationManager notificationManager;

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread()
        {
            public void run() {
                searchInBackgroundAndNotify();
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void searchInBackgroundAndNotify(){
        while (true){
            int millisecondsToSleep = 5000; //TODO: TEST, to be changed with 600000
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

            if(isConnected) {
                File f = new File(DirectoryManager.getAppDir() + "config.txt");

                if (f.exists()) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        boolean enabled = Boolean.parseBoolean(br.readLine());
                        millisecondsToSleep = Integer.parseInt(br.readLine());

                        if (enabled && DirectoryManager.wishlistExists()) {
                            Games gs = DirectoryManager.importGames();
                            if(gs!=null) {
                                for (GamePreview gp : gs) {
                                    Game game = (Game) gp;
                                    //List<String> notifications = game.update();

                                    //TODO
                                    List<String> notifications = new ArrayList<String>();
                                    notifications.add("Gioco in sconto");

                                    if (notifications != null) {

                                        for (String str : notifications) {

                                            //Read notification id
                                            try {
                                                BufferedReader br2 = new BufferedReader(new FileReader(DirectoryManager.getAppDir()+"notificationId.txt"));
                                                notificationId = Integer.parseInt(br2.readLine());
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            Notification.Builder notification = new Notification.Builder(this)
                                                    .setSmallIcon(R.drawable.notification_icon)
                                                    .setContentTitle(game.getTitle())
                                                    .setContentText(str);

                                            Intent intent = new Intent(this, ActivityMain.class);
                                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                                            notification.setContentIntent(pendingIntent);

                                            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            notificationManager.notify(notificationId, notification.build());

                                            notificationId++;


                                            //Write notification id
                                            try {
                                                BufferedWriter bw2 = new BufferedWriter(new FileWriter(DirectoryManager.getAppDir()+"notificationId.txt"));
                                                bw2.write(""+notificationId);
                                                bw2.newLine();
                                                bw2.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            try {
                Thread.sleep(millisecondsToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}