package com.example.gpstracker.services

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.gpstracker.App
import com.example.gpstracker.MainActivity
import com.example.gpstracker.R
import com.example.gpstracker.managers.CacheManager
import com.example.gpstracker.workers.SaveLocationWorker
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ForegroundService : Service() {

    @Inject
    lateinit var pendingIntent: PendingIntent
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var locationManager: LocationManager
    @Inject
    lateinit var cacheManager: CacheManager

    private val foregroundServiceScope = Job()
    private val binder = Binder()
    private val CHANNEL_ID_FOREGROUND = "GPSTrackerForeground"
    private val NOTIFICATION_ID_FOREGROUND = 564564248

    companion object {
        var isActive: Boolean = false
    }

    inner class Binder : android.os.Binder() {
        fun getService(): ForegroundService = this@ForegroundService
    }

    override fun onCreate() {
        (applicationContext as App).appComponent.inject(this)
        isActive = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID_FOREGROUND
            val descriptionText = "Ты получил уведомление"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_FOREGROUND, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_FOREGROUND)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Запущен трекер вашего положения")
            .setContentText("Запущен трекер вашего положения")
            .build()
        startForeground(NOTIFICATION_ID_FOREGROUND, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val saveLocationRequest = PeriodicWorkRequestBuilder<SaveLocationWorker>(30, TimeUnit.SECONDS).build()
//        WorkManager.getInstance(applicationContext).enqueue(saveLocationRequest)
        CoroutineScope(Dispatchers.IO + foregroundServiceScope).launch {
            while (true) {
                delay(30000)
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    continue
                }
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: continue
                cacheManager.setLocation(location)
                Log.i(ForegroundService::class.java.name, "data = ${cacheManager.getData()}")
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        isActive = false
        foregroundServiceScope.cancel()
        super.onDestroy()
    }
}