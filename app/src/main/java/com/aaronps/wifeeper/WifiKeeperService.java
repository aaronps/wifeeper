package com.aaronps.wifeeper;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class WifiKeeperService extends Service {
    private static final String TAG = "WifiKeeperService";

    private WifiWakeKeeper mKeeper;

    @Override
    public void onCreate() {
        mKeeper = new WifiWakeKeeper(this, "Wifeeper");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String command = (intent != null) ? intent.getStringExtra("command") : null;

        if (command == null)
        {
            if (!mKeeper.isLocking())
            {
                if (mKeeper.lock())
                {
                    startForeground(1, createNotification());
                }
            }
        } else if ("stop".equals(command))
        {
            Log.d(TAG, "Stopping meself");
            stopForeground(true);
            stopSelf(); // If not mistaken, android will call onDestroy after return
            return Service.START_NOT_STICKY;
        }
        // @todo maybe notify weird commands received.

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
    }

    @Override
    public void onDestroy() {
        mKeeper.release();
    }

    private Notification createNotification() {
        Notification.Builder nb = new Notification.Builder(this);
        nb.setSmallIcon(R.drawable.ic_stat_service_working)
//                .setLargeIcon(R.mipmap.ic_launcher)
                .setContentTitle("Wifeeper")
                .setContentText(getRandomContent());

        Intent stopIntent = new Intent(this, getClass());
        stopIntent.putExtra("command", "stop");

        nb.setContentIntent(PendingIntent.getService(this, 0, stopIntent, 0));

        return nb.getNotification(); // API level 11
//        return nb.build(); // @todo (future)replace to this when using API level 16
    }

    private static String getRandomContent() {
        return QUOTES[new Random(System.currentTimeMillis()).nextInt(QUOTES.length)];
    }

    private static final String[] QUOTES = {
            "Keeping the Wifee",
            "Keeping it On!",
            "Dominating the CPU"
    };
}
