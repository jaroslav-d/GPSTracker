package com.example.gpstracker.services

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.gpstracker.App
import com.example.gpstracker.R
import kotlinx.coroutines.*
import javax.inject.Inject

class NotificationService : Service() {

    @Inject
    lateinit var pendingIntent: PendingIntent
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var locationManager: LocationManager

    private lateinit var builder: NotificationCompat.Builder
    private val notificationServiceScope = Job()
    private val notificationLayout by lazy { RemoteViews(packageName, R.layout.notification_main) }
    private val binder = Binder()
    private val CHANNEL_ID = "GPSTrackerInfo"
    private val NOTIFICATION_ID = 564564249

    inner class Binder : android.os.Binder() {
        fun getService(): NotificationService = this@NotificationService
    }

    override fun onCreate() {
        (applicationContext as App).appComponent.inject(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val descriptionText = "Ты получил уведомление"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
        notificationLayout.setTextViewText(R.id.latitude, "latitude: нет данных")
        notificationLayout.setTextViewText(R.id.longitude, "longitude: нет данных")
        builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO + notificationServiceScope).launch {
            while (true) {
                delay(10000)
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
                notificationLayout.setTextViewText(R.id.latitude, "latitude: ${location.latitude}")
                notificationLayout.setTextViewText(R.id.longitude, "longitude: ${location.longitude}")
                builder.setCustomContentView(notificationLayout)
                notificationManager.notify(NOTIFICATION_ID, builder.build())
                Log.i(NotificationService::class.java.name, "выполнил работу, задача идет на повтор")
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        notificationServiceScope.cancel()
        super.onDestroy()
    }
}