package com.example.gpstracker.di

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.gpstracker.MainActivity
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule {

    @Singleton
    @Provides
    fun provideLocationManager(context: Context) = context.getSystemService(Service.LOCATION_SERVICE) as LocationManager

    @Singleton
    @Provides
    fun providePendingIntent(context: Context) = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)

    @Singleton
    @Provides
    fun provideNotificationManager(context: Context) = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}