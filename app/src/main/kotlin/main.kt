/**
 * Created by krom on 5/28/17.
 */
package com.aaronps.wifeeper

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.util.Log

const val appName = "Wifeeper"

class ServiceStarter : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, WifiKeeperService::class.java)
        startService(intent)
        finish()
    }
}

class WifiWakeKeeper(ctx: Context, Name: String) {

    private val mWifiLock: WifiManager.WifiLock
    private val mWakeLock: PowerManager.WakeLock

    init {
        val powerManager = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wifiManager = ctx.getSystemService(Context.WIFI_SERVICE) as WifiManager

        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Name + "-cpu")
        mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, Name + "-wifi")
    }

    val isLocking: Boolean
        get() = mWakeLock.isHeld && mWifiLock.isHeld

    fun lock(): Boolean {
        if (!mWakeLock.isHeld) mWakeLock.acquire()
        if (!mWifiLock.isHeld) mWifiLock.acquire()

        return isLocking
    }

    fun release(): Boolean {
        if (mWifiLock.isHeld) mWifiLock.release()
        if (mWakeLock.isHeld) mWakeLock.release()

        return !isLocking
    }

}

class WifiKeeperService : Service() {

    // this should crash ... and it does (NPE)
//    private val mKeeper = WifiWakeKeeper(this, "Wifeeper")

//    private lateinit var mKeeper : WifiWakeKeeper
    private val mKeeper : WifiWakeKeeper by lazy { WifiWakeKeeper(this, appName) }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        // or use this one with lateinit?
//        mKeeper = WifiWakeKeeper(this, "Wifeeper")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
            when (intent?.getStringExtra("command")) {
                null -> {
                    if (!mKeeper.isLocking) {
                        if (mKeeper.lock()) {
                            val stopIntent = Intent(this, this.javaClass)
                            stopIntent.putExtra("command", "stop")

                            startForeground(1, createNotification(this, stopIntent))
                        }
                    }
                    Service.START_STICKY
                }
                "stop" -> {
                    Log.d("WifiKeeperService", "Stopping myself")
                    stopForeground(true)
                    stopSelf() // If not mistaken, android will call onDestroy after return
                    Service.START_NOT_STICKY
                }
            // @todo maybe notify weird commands received.
                else -> Service.START_STICKY
            }
}
