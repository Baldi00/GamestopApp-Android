package com.gamestop.android.gamestopapp;

import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class BackgroundService extends Service {
    private int notificationId;
    private SettingsManager settingsManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            settingsManager = SettingsManager.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            int notificationServiceSleepTime = 600000;
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

            //Check immediately when goes online
            while (!isConnected){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activeNetwork = cm.getActiveNetworkInfo();
                isConnected = activeNetwork != null && activeNetwork.isConnected();
            }

            try {
                boolean notificationServiceEnabled = settingsManager.isNotificationServiceEnabled();
                notificationServiceSleepTime = settingsManager.getNotificationServiceSleepTime();
                boolean notificationSoundEnabled = settingsManager.isNotificationSoundEnabled();

                if (notificationServiceEnabled && DirectoryManager.wishlistExists() && !DirectoryManager.wishlistEmpty()) {

                    Games gs = DirectoryManager.importGames();

                    if(gs!=null) {
                        for (GamePreview gp : gs) {
                            Game game = (Game) gp;

                            //Update every game
                            List<String> notifications = game.update();

                            if (notifications != null) {

                                for (String str : notifications) {

                                    //Read notification id
                                    BufferedReader br2 = new BufferedReader(new FileReader(DirectoryManager.getAppDir()+"notificationId.txt"));
                                    notificationId = Integer.parseInt(br2.readLine());

                                    Intent resultIntent = new Intent(this, ActivityGamePage.class);
                                    resultIntent.putExtra("source","wishlist");
                                    resultIntent.putExtra("id",game.getId());

                                    // Create the TaskStackBuilder and add the intent, which inflates the back stack
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                                    stackBuilder.addNextIntentWithParentStack(resultIntent);

                                    // Get the PendingIntent containing the entire back stack
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);

                                    NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "GAMESTOPAPP");
                                    notification.setContentIntent(resultPendingIntent);
                                    notification.setSmallIcon(R.drawable.notification_icon);
                                    notification.setContentTitle(game.getTitle());
                                    notification.setContentText(str);

                                    if(notificationSoundEnabled){
                                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        notification.setSound(alarmSound);
                                    }

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                                    notificationManager.notify(notificationId, notification.build());

                                    notificationId++;


                                    //Write notification id
                                    BufferedWriter bw2 = new BufferedWriter(new FileWriter(DirectoryManager.getAppDir()+"notificationId.txt"));
                                    bw2.write(""+notificationId);
                                    bw2.newLine();
                                    bw2.close();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {     // TODO : try to remove
                e.printStackTrace();
            }

            ActivityMain.updateWishlist();

            //Wait until next check
            try {
                Thread.sleep(notificationServiceSleepTime);
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
