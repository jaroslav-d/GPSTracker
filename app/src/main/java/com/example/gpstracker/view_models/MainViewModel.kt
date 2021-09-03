package com.example.gpstracker.view_models

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.gpstracker.databinding.FragmentMainBinding
import com.example.gpstracker.services.ForegroundService
import kotlinx.coroutines.*

class MainViewModel(private val binding: FragmentMainBinding) : LocationListener {

    enum class State(val startButton: Int, val stopButton: Int) {
        SERVICES_ON(View.GONE, View.VISIBLE),
        SERVICES_OFF(View.VISIBLE, View.GONE);
    }

    private val viewModelScope = Job()

    init {
        CoroutineScope(Dispatchers.Main + viewModelScope).launch {
            while (true) {
                if (ForegroundService.isActive) setStatus(State.SERVICES_ON) else setStatus(State.SERVICES_OFF)
                Log.i(MainViewModel::class.java.name, "Проверка статуса сервисов. Статус: ${ForegroundService.isActive}")
                delay(1000)
            }
        }
    }

    fun destroy() {
        viewModelScope.cancel()
    }

    private fun setStatus(state: State) {
        binding.startServices.visibility = state.startButton
        binding.stopServices.visibility = state.stopButton
    }

    override fun onLocationChanged(location: Location) {
        binding.location.text = "latitude: ${location.latitude} \nlongitude: ${location.longitude}"
    }

    override fun onProviderDisabled(provider: String) = Unit
    override fun onProviderEnabled(provider: String) = Unit
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

}