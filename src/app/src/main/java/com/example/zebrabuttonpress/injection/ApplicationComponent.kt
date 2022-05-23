package com.example.zebrabuttonpress.injection

import android.content.Context
import com.example.zebrabuttonpress.ui.MainActivity
import com.example.zebrabuttonpress.ui.MainForegroundService
import com.example.zebrabuttonpress.ui.home.HomeFragment
import com.example.zebrabuttonpress.ui.splash.SplashFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, NetworkModule::class, ViewModelModule::class])
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
    fun inject(mainActivity: MainActivity)

    fun inject(mainService: MainForegroundService)

    fun inject(splashFragment: SplashFragment)

    fun inject(homeFragment: HomeFragment)
}