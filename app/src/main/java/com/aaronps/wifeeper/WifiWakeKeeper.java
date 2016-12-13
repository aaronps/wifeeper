package com.aaronps.wifeeper;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

/**
 * Created by krom on 12/12/16.
 */

public class WifiWakeKeeper {

    private final WifiManager.WifiLock mWifiLock;
    private final PowerManager.WakeLock mWakeLock;

    public WifiWakeKeeper(final Context ctx, final String Name) {
        final PowerManager powerManager = (PowerManager)ctx.getSystemService(Context.POWER_SERVICE);
        final WifiManager  wifiManager  = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);

        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Name + "-cpu");
        mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, Name + "-wifi");
    }

    public final boolean isLocking() {
        return mWakeLock.isHeld() && mWifiLock.isHeld();
    }

    public final boolean lock() {
        if ( ! mWakeLock.isHeld() ) mWakeLock.acquire();
        if ( ! mWifiLock.isHeld() ) mWifiLock.acquire();

        return isLocking();
    }

    public final boolean release() {
        if ( mWifiLock.isHeld() ) mWifiLock.release();
        if ( mWakeLock.isHeld() ) mWakeLock.release();

        return ! isLocking();
    }

}
