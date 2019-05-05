package com.gamestop.android.gamestopapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout pullToRefresh;
    private ActivityMain main;
    private int notificationId;
    private SettingsManager settingsManager;
    private int numNotifications;

    public MyOnRefreshListener(SwipeRefreshLayout pullToRefresh, ActivityMain main) {
        this.pullToRefresh = pullToRefresh;
        this.main = main;
        try {
            settingsManager = SettingsManager.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Call the update for the games in wishlist
    @Override
    public void onRefresh() {
        numNotifications = 0;
        Updater up = new Updater(main,this);
        up.execute();
    }

    private void onEndRefresh(Boolean result){
        pullToRefresh.setRefreshing(false);
        ActivityMain.updateWishlist();


        if(result!=null)
            if(result) {
                Toast.makeText(main, "Aggiornamento terminato correttamente", Toast.LENGTH_SHORT).show();
                if(numNotifications==0){
                    Toast.makeText(main, "Non ci sono novità", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(main, "Non sei connesso a internet", Toast.LENGTH_SHORT).show();
            }
    }

    private class Updater extends AsyncTask {
        private ActivityMain main;
        MyOnRefreshListener refreshListener;

        public Updater(ActivityMain main, MyOnRefreshListener refreshListener){
            this.main = main;
            this.refreshListener = refreshListener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {

                if ( !DirectoryManager.wishlistExists() ) { return null; }
                if ( DirectoryManager.wishlistEmpty() ) { return null; }

                ConnectivityManager cm = (ConnectivityManager) main.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

                if (isConnected) {
                    boolean notificationSoundEnabled = settingsManager.isNotificationSoundEnabled();

                    if (DirectoryManager.wishlistExists() && !DirectoryManager.wishlistEmpty()) {
                        Games gs = DirectoryManager.importGames();

                        if (gs != null) {
                            for (GamePreview gp : gs) {
                                Game game = (Game) gp;
                                List<String> notifications = game.update();
                                notifications.add("CIAO");

                                if (notifications != null) {

                                    for (String str : notifications) {

                                        numNotifications++;

                                        //Read notification id
                                        BufferedReader br = new BufferedReader(new FileReader(DirectoryManager.getAppDir() + "notificationId.txt"));
                                        notificationId = Integer.parseInt(br.readLine());

                                        Intent resultIntent = new Intent(main, ActivityGamePage.class);
                                        resultIntent.putExtra("source", "wishlist");
                                        resultIntent.putExtra("id", game.getId());
                                        // Create the TaskStackBuilder and add the intent, which inflates the back stack
                                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(main);
                                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                                        // Get the PendingIntent containing the entire back stack
                                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);


                                        NotificationCompat.Builder notification = new NotificationCompat.Builder(main, "Gamestop");
                                        notification.setContentIntent(resultPendingIntent);
                                        notification.setSmallIcon(R.drawable.notification_icon);
                                        notification.setContentTitle(game.getTitle());
                                        notification.setContentText(str);

                                        if (notificationSoundEnabled) {
                                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            notification.setSound(alarmSound);
                                        }

                                        NotificationManager notificationManager = (NotificationManager) main.getSystemService(Context.NOTIFICATION_SERVICE);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            NotificationChannel channel = new NotificationChannel("Gamestop","Gamestop",NotificationManager.IMPORTANCE_DEFAULT);
                                            notificationManager.createNotificationChannel(channel);
                                        }

                                        notificationManager.notify(notificationId, notification.build());

                                        notificationId++;

                                        //Write notification id
                                        BufferedWriter bw = new BufferedWriter(new FileWriter(DirectoryManager.getAppDir() + "notificationId.txt"));
                                        bw.write("" + notificationId);
                                        bw.newLine();
                                        bw.close();
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }

            } catch (Exception e) {         // TODO: try to remove
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Object o) {
            refreshListener.onEndRefresh((Boolean)o);
        }
    }
}
