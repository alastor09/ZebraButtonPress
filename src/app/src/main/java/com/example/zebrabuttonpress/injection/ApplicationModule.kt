package com.example.zebrabuttonpress.injection

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.example.zebrabuttonpress.util.DefaultDispatcherProvider
import com.example.zebrabuttonpress.util.DispatcherProvider
import com.example.zebrabuttonpress.util.ResourcesProvider
import com.example.zebrabuttonpress.util.ResourcesProviderImpl
import com.example.zebrabuttonpress.util.notification.NotificationsManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApplicationModule {
    @Provides
    @Singleton
    fun provideDispatchers(dispatchers: DefaultDispatcherProvider): DispatcherProvider = dispatchers

    @Singleton
    @Provides
    fun provideResourcesProvider(provider: ResourcesProviderImpl): ResourcesProvider = provider

    @Provides
    @Singleton
    fun provideNotificationManagerCompat(context: Context): NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    @Provides
    @Singleton
    fun provideNotificationManager(
        context: Context, notificationManagerCompat:
        NotificationManagerCompat
    ): NotificationsManager =
        NotificationsManager(context, notificationManagerCompat)

    //region Services
    //endregion
}