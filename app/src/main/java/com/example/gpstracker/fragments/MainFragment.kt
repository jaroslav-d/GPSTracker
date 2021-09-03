package com.example.gpstracker.fragments

import android.Manifest
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
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)
        viewModel = MainViewModel(binding)
        binding.startServices.setOnClickListener {
            requireContext().startService(Intent(requireContext(), ForegroundService::class.java))
            requireContext().startService(Intent(requireContext(), NotificationService::class.java))
        }
        binding.stopServices.setOnClickListener {
            requireContext().stopService(Intent(requireContext(), ForegroundService::class.java))
            requireContext().stopService(Intent(requireContext(), NotificationService::class.java))
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