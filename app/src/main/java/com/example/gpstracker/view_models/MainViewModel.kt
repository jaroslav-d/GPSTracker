package com.example.gpstracker.view_models

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import com.example.gpstracker.R
import com.example.gpstracker.databinding.FragmentMainBinding
import com.example.gpstracker.services.ForegroundService
import kotlinx.coroutines.*

class MainViewModel(private val binding: FragmentMainBinding) : LocationListener {

    enum class State(val isSelected: Boolean, val isSelectedButton: Boolean, val text: Int) {
        SERVICES_ON(true, false, R.string.stop_services),
        SERVICES_OFF(false, true, R.string.start_services);
    }

    private val viewModelScope = CoroutineScope(Dispatchers.Main + Job()).launch {
        while (true) {
            Log.i(MainViewModel::class.java.name, "Проверка статуса сервисов. Статус: ${ForegroundService.isActive}")
            setStatus( State.values().find { it.isSelected == ForegroundService.isActive }!! )
            delay(5000)
        }
    }

    fun destroy() {
        viewModelScope.cancel()
    }

    private fun setStatus(state: State) {
        binding.power.isSelected = state.isSelectedButton
        binding.power.text = binding.root.context.resources.getText(state.text)
    }

    override fun onLocationChanged(location: Location) {
        binding.location.text = "latitude: ${location.latitude} \nlongitude: ${location.longitude}"
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(MainViewModel::class.java.name, "onProviderDisabled: ")
    }
    override fun onProviderEnabled(provider: String) {
        Log.d(MainViewModel::class.java.name, "onProviderEnabled: ")
    }
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d(MainViewModel::class.java.name, "onStatusChanged: ")
    }

}