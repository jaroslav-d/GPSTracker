package com.example.gpstracker.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.gpstracker.App
import com.example.gpstracker.managers.CacheManager
import javax.inject.Inject

class SaveLocationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    @Inject
    lateinit var mLocationManager: LocationManager
    @Inject
    lateinit var cacheManager: CacheManager

    init {
        (appContext as App).appComponent.inject(this)
    }

    override fun doWork(): Result {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.retry()
        }
        val location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: return Result.retry()
        cacheManager.setLocation(location)
        Log.i(SaveLocationWorker::class.java.name, "data = ${cacheManager.getData()}")
        return Result.success()
    }
}