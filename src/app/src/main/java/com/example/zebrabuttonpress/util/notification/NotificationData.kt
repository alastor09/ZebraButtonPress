package com.example.zebrabuttonpress.util.notification

import android.os.Bundle

/**
 * Object holding information to help build up a notification
 */
data class NotificationData(
    val title: String, // notification title
    val channel: NotificationsManager.Companion.Channel = NotificationsManager.Companion.Channel.CHANNEL_GENERAL, // notification channel
    val group: NotificationsManager.Companion.Group = NotificationsManager.Companion.Group.GROUP_DEFAULT, // code to group notifications
    val silent: Boolean = false, // silent or displayed notification?
    val message: String? = null, // notification message
    val targetActivity:Class<*>? = null, // activity to open when tapping this notification
    val payload: Bundle? = null, // the optional extra payload to send to the target
    val actions: List<NotificationActionData>? = null, // the optional extra actions
    val id: Int? = null, // an optional id. Group value is used otherwise
    val onGoing: Boolean = false // true if the notification can't be swiped off
)