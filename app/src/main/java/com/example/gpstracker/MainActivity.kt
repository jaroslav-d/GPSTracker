package com.example.gpstracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.gpstracker.services.ForegroundService
import com.example.gpstracker.services.NotificationService
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    @Inject
    lateinit var mLocationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as App).appComponent.inject(this)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                1234
            )
        }

        if (ForegroundService.isActive) return
        startService(Intent(this, ForegroundService::class.java))
        startService(Intent(this, NotificationService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != 1234) finish()
        grantResults.forEach { if (it == PackageManager.PERMISSION_DENIED) finish() }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}