package com.example.gpstracker

import android.app.Application
import com.example.gpstracker.di.AppComponent
import com.example.gpstracker.di.DaggerAppComponent

class App : Application() {
    val appComponent: AppComponent by lazy { DaggerAppComponent.factory().create(applicationContext) }
}