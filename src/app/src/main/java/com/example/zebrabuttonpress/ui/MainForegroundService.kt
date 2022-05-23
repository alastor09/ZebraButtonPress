package com.example.zebrabuttonpress.ui

import androidx.lifecycle.LifecycleService
import com.example.zebrabuttonpress.ui.helper.extension.app
import com.example.zebrabuttonpress.util.notification.NotificationsManager
import javax.inject.Inject

class MainForegroundService : LifecycleService() {
    @Inject
    lateinit var viewModel: MainServiceViewModel

    @Inject
    lateinit var notificationsManager: NotificationsManager

    override fun onCreate() {
        super.onCreate()
        app.appComponent.inject(this)

        startForeground(1, notificationsManager.getMainServiceNotification())

        viewModel.onInit()
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.onDestroy()
    }
}

