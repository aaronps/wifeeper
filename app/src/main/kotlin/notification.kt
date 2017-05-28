/**
 * Created by krom on 5/28/17.
 */
package com.aaronps.wifeeper

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

private val QUOTES = arrayOf(
        "Keeping the Wifee",
        "Keeping it On!",
        "Dominating the CPU"
)

private fun getRandomQuote(): String {
    return QUOTES[Random(System.currentTimeMillis()).nextInt(QUOTES.size)]
}


internal fun createNotification(context: Context, intent: Intent): Notification {
    val nb = Notification.Builder(context)

    nb.setSmallIcon(R.drawable.ic_stat_service_working)
            //.setLargeIcon(R.mipmap.ic_launcher)
            .setContentTitle(appName)
            .setContentText(getRandomQuote())

    nb.setContentIntent(PendingIntent.getService(context, 0, intent, 0))

    return nb.notification // API level 11
//    return nb.build(); // @todo (future)replace to this when using API level 16
}

