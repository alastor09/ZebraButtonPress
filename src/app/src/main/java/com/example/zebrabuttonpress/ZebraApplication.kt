package com.example.zebrabuttonpress

import android.app.Application
import com.example.zebrabuttonpress.injection.ApplicationComponent
import com.example.zebrabuttonpress.injection.DaggerApplicationComponent
import com.example.zebrabuttonpress.util.logging.FileLoggingTree
import timber.log.Timber

class ZebraApplication : Application() {

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        Timber.plant(FileLoggingTree(context = applicationContext))
    }
}