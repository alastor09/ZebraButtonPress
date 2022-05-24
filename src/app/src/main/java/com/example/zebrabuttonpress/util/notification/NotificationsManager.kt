package com.example.zebrabuttonpress.util.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.example.zebrabuttonpress.R
import com.example.zebrabuttonpress.ui.MainActivity
import com.example.zebrabuttonpress.util.StatusHandler
import timber.log.Timber

/**
 * Manages notification data/behaviour based on server notification data
 */
class NotificationsManager(
    val context: Context,
    val notificationManager: NotificationManagerCompat
) {

    companion object {
        // notifications can be managed by channels at the Android level
        enum class Channel {
            CHANNEL_ZEBRA,
        }

        // used to group notification in the tray
        enum class Group(val value: Int) {
            GROUP_DEFAULT(100),
        }
    }

    init {
        val channelGeneral = NotificationChannel(
            Channel.CHANNEL_ZEBRA.name,
            context.getString(R.string.notification_channel_general),
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(channelGeneral)

    }
    /**
     * Special method to get thenotification for the foregroundservice
     */
    fun getMainServiceNotification(): Notification = NotificationCompat.Builder(context, Channel.CHANNEL_ZEBRA.name)
        .setContentTitle(context.getString(R.string.notification_foreground_service_title))
        .setContentText(context.getString(R.string.notification_foreground_service_message))
        .setSmallIcon(R.drawable.ic_notification)
        .build()

}