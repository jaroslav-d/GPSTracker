package com.example.gpstracker.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.gpstracker.App
import com.example.gpstracker.databinding.FragmentMainBinding
import com.example.gpstracker.services.ForegroundService
import com.example.gpstracker.services.NotificationService
import com.example.gpstracker.view_models.MainViewModel
import javax.inject.Inject

class MainFragment : Fragment() {

    enum class Command(val isSelected: Boolean, val execute: Context.() -> Unit) {
        ON(true, {
            startService(Intent(this, ForegroundService::class.java))
            startService(Intent(this, NotificationService::class.java))
        }),
        OFF(false, {
            stopService(Intent(this,ForegroundService::class.java))
            stopService(Intent(this, NotificationService::class.java))
        })
    }

    lateinit var binding: FragmentMainBinding
    @Inject
    lateinit var mLocationManager: LocationManager
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        viewModel = MainViewModel(binding)
        binding.power.setOnClickListener {
            val cmds = Command.values().filter { cmd -> cmd.isSelected == it.isSelected }
            cmds.forEach { cmd -> cmd.execute(requireContext()) }
            it.isSelected = !it.isSelected
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 1f, viewModel)
    }

    override fun onDestroy() {
//        mLocationManager.removeUpdates(viewModel)
        viewModel.destroy()
        super.onDestroy()
    }

}