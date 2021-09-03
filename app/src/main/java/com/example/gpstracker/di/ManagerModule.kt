package com.example.gpstracker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.gpstracker.managers.DataSenderManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ManagerModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideRetrofitRemoteService(): DataSenderManager {
        return Retrofit.Builder()
            .baseUrl("https://gpstracker-01-09-21-default-rtdb.firebaseio.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DataSenderManager::class.java)
    }
}