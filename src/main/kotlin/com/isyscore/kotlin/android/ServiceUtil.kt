@file:Suppress("unused")

package com.isyscore.kotlin.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

abstract class ForegroundService: Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        goForeground()
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }

    abstract fun serviceNotification(callback:(notifyId: Int, channelName: String, title: String, iconResId: Int) -> Unit)

    private fun goForeground() {
        if (Build.VERSION.SDK_INT >= 26) {
            serviceNotification { notifyId, channelName, title, iconResId ->
                val channel = NotificationChannel(channelName, channelName, NotificationManager.IMPORTANCE_NONE)
                (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.createNotificationChannel(channel)
                val notification = Notification.Builder(this, channelName)
                    .setContentTitle(title)
                    .setSmallIcon(iconResId)
                    .build()
                startForeground(notifyId, notification)
            }
        }
    }
}
