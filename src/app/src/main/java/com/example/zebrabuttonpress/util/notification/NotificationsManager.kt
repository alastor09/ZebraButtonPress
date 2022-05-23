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
            CHANNEL_GENERAL,
            CHANNEL_SERVICE,
            CHANNEL_STATUS
        }

        // used to group notification in the tray
        enum class Group(val value: Int) {
            GROUP_DEFAULT(100),
            GROUP_SERVICE(101),
            GROUP_STATUS(102),
        }

        const val STATUS_SUMMARY_ID = 888
        const val FALL_DETECTED = 890
    }

    init {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the channels
            val channelGeneral = NotificationChannel(
                Channel.CHANNEL_GENERAL.name,
                context.getString(R.string.notification_channel_general),
                NotificationManager.IMPORTANCE_HIGH
            )

            val channelService = NotificationChannel(
                Channel.CHANNEL_SERVICE.name,
                context.getString(R.string.notification_channel_service),
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val channelStatus = NotificationChannel(
                Channel.CHANNEL_STATUS.name,
                context.getString(R.string.notification_channel_status),
                NotificationManager.IMPORTANCE_HIGH
            )


            notificationManager.createNotificationChannel(channelGeneral)
            notificationManager.createNotificationChannel(channelService)
            notificationManager.createNotificationChannel(channelStatus)
        }
    }

    /**
     * Special method to get thenotification for the foregroundservice
     */
    fun getMainServiceNotification(): Notification = NotificationCompat.Builder(context, Channel.CHANNEL_GENERAL.name)
        .setContentTitle(context.getString(R.string.notification_foreground_service_title))
        .setContentText(context.getString(R.string.notification_foreground_service_message))
        .setSmallIcon(R.drawable.ic_notification)
        .build()

    /**
     * Sending Status notification
     */
    fun sendStatusNotification(handler: StatusHandler) {
        Timber.d(("*** ${handler.hashCode()}"))
        val summaryBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, Channel.CHANNEL_STATUS.name)
                .setGroup(Group.GROUP_STATUS.name)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)

        sendNotification(
            NotificationData(
                id = handler.hashCode(),
                title = context.getString(handler.title),
                message = context.getString(handler.message),
                channel = Channel.CHANNEL_STATUS,
                group = Group.GROUP_STATUS,
                onGoing = true
            )
        )

        notificationManager.notify(STATUS_SUMMARY_ID, summaryBuilder.build())
    }

    /**
     * Setting and displaying the actual UI notification
     */
    private fun sendNotification(notificationData: NotificationData) {
        // create intent using specified target activity
        val intent: Intent = Intent(
            context, notificationData.targetActivity ?: MainActivity::class.java
        )
        // add extras if we have any
        notificationData.payload?.let {
            intent.putExtras(it)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = getNotificationPendingIntent(notificationData.targetActivity, intent)

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, notificationData.channel.name)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notificationData.title)
                .setContentIntent(pendingIntent)
                .setGroup(notificationData.group.name)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setOngoing(notificationData.onGoing)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationData.message?.let {
            notificationBuilder.setContentText(notificationData.message)
            notificationBuilder.setStyle(
                NotificationCompat.BigTextStyle() // make notification expandable
                    .bigText(notificationData.message)
            )
        }

        // add action buttons, if any
        notificationData.actions?.forEach {
            addNotificationAction(notificationBuilder, it)
        }

        notificationManager.notify(
            notificationData.id ?: notificationData.group.value,
            notificationBuilder.build()
        )
    }

    /**
     * Building pending intent to specify which activity to open when the user hits the notification
     * It uses the stack builder, so if the activity has define its parent in its xml definition then
     * hitting the back button will work as expected
     */
    private fun getNotificationPendingIntent(
        targetActivity: Class<*>?,
        intent: Intent
    ): PendingIntent {
        val stackBuilder = TaskStackBuilder.create(context)
        if (targetActivity != null) stackBuilder.addParentStack(targetActivity)
        stackBuilder.addNextIntent(intent)

        return stackBuilder.getPendingIntent(
            System.currentTimeMillis().toInt(),
            PendingIntent.FLAG_UPDATE_CURRENT
        )!!
    }

    /**
     * Adds an action button to the notification
     */
    private fun addNotificationAction(
        notificationBuilder: NotificationCompat.Builder,
        notificationActionData: NotificationActionData
    ) {
        if (notificationActionData.targetActivity != null) {
            val intent = Intent(context, notificationActionData.targetActivity)
            intent.action = notificationActionData.actionName
            // add extras if we have any
            notificationActionData.payload?.let {
                intent.putExtras(it)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val pendingIntent =
                getNotificationPendingIntent(notificationActionData.targetActivity, intent)

            notificationBuilder.addAction(
                NotificationCompat.Action(
                    notificationActionData.icon,
                    context.getString(notificationActionData.text),
                    pendingIntent
                )
            )
        } else if (notificationActionData.targetBrodcast != null) {
            val intent = Intent(context, notificationActionData.targetBrodcast).apply {
                action = notificationActionData.actionName
                notificationActionData.payload?.let {
                    putExtras(it)
                }
            }

            val pendingIntent: PendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, 0)

            notificationBuilder.addAction(
                NotificationCompat.Action(
                    notificationActionData.icon,
                    context.getString(notificationActionData.text),
                    pendingIntent
                )
            )
        }
    }

    /**
     * Clear all notifications
     */
    fun clearAllNotifications() {
        notificationManager.cancelAll()
    }

    fun clearNotificationStatus(handler: StatusHandler, clearSummary: Boolean) {
        notificationManager.cancel(handler.hashCode())
        if (clearSummary) notificationManager.cancel(STATUS_SUMMARY_ID)
    }

    fun clearNotification(id: Int) {
        notificationManager.cancel(id)
    }
}