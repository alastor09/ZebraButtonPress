package com.example.zebrabuttonpress

import android.app.Application
import androidx.databinding.library.BuildConfig
import com.example.zebrabuttonpress.injection.ApplicationComponent
import com.example.zebrabuttonpress.injection.DaggerApplicationComponent
import timber.log.Timber

class ZebraApplication : Application() {

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}