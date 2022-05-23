package com.example.zebrabuttonpress.util.notification

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Data for notification button
 */
data class NotificationActionData(
    @DrawableRes val icon: Int,
    @StringRes val text: Int,
    val targetActivity: Class<*>? = null,
    val targetBrodcast: Class<*>? = null,
    val payload: Bundle? = null,
    val actionName: String = ""
)


