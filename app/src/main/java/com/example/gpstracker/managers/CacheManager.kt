package com.example.gpstracker.managers

import android.content.SharedPreferences
import android.location.Location
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CacheManager @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val DATA = "data"
        const val DEFAULT_DATA = ""
    }

    fun getData(): String {
        return "[ ${ prefs.getString(DATA, null) ?: DEFAULT_DATA } ]"
    }

    fun clearData() {
        prefs.edit().clear().apply()
    }

    fun setLocation(location: Location) {
        val data = prefs.getString(DATA, null)
        val date = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(Date(System.currentTimeMillis()))
        val time = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date(System.currentTimeMillis()))
        val obj = "{date: $date, time: $time, latitude: ${location.latitude}, longitude: ${location.longitude}}"
        if (data == null) {
            prefs.edit().putString(DATA, obj).apply()
        } else {
            prefs.edit().putString(DATA, "$data, $obj").apply()
        }
    }

    fun setLocations(vararg location: Location) {
        val data = prefs.getString(DATA, null)
        val objects = location.joinToString {
            val date = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(Date(System.currentTimeMillis()))
            val time = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date(System.currentTimeMillis()))
            "{date: $date, time: $time, latitude: ${it.latitude}, longitude: ${it.longitude}}"
        }
        if (data == null) {
            prefs.edit().putString(DATA, objects).apply()
        } else {
            prefs.edit().putString(DATA, "$data, $objects").apply()
        }
    }
}