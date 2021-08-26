package com.example.gpstracker.di

import android.content.Context
import com.example.gpstracker.MainActivity
import com.example.gpstracker.fragments.MainFragment
import com.example.gpstracker.services.ForegroundService
import com.example.gpstracker.services.NotificationService
import com.example.gpstracker.workers.SaveLocationWorker
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [LocationModule::class, ManagerModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)
    fun inject(service: ForegroundService)
    fun inject(service: NotificationService)
    fun inject(worker: SaveLocationWorker)
}