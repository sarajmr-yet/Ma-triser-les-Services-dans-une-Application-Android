package com.sara.timerserviceapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class TimerBackgroundService extends Service {

    private final IBinder binder = new TimerBinder();

    private int counter = 0;
    private Timer timer;
    private boolean running = false;

    private static final String CHANNEL_ID = "timer_channel";
    private static final int ID_NOTIFICATION = 222;

    public class TimerBinder extends Binder {

        TimerBackgroundService getService() {

            return TimerBackgroundService.this;
        }
    }

    @Override
    public void onCreate() {

        super.onCreate();

        createChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!running) {

            running = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                startForeground(
                        ID_NOTIFICATION,
                        buildNotification(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                );

            } else {

                startForeground(ID_NOTIFICATION, buildNotification());
            }

            launchTimer();
        }

        return START_STICKY;
    }

    private void launchTimer() {

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                counter++;

                updateNotification();
            }

        }, 0, 1000);
    }

    private Notification buildNotification() {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Timer Service")
                        .setContentText("Compteur : " + counter + " sec")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setOngoing(true);

        return builder.build();
    }

    private void updateNotification() {

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(ID_NOTIFICATION, buildNotification());
    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Timer Service",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription("Foreground Timer Service");

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            if (manager != null) {

                manager.createNotificationChannel(channel);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    @Override
    public void onDestroy() {

        if (timer != null) {

            timer.cancel();
        }

        running = false;

        stopForeground(true);

        super.onDestroy();
    }
}