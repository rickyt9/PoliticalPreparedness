package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.*

class DetailFragment : Fragment() {

    private val TAG = "RepresentativeFragment"

    companion object {
        private const val REQUEST_LOCATION_PERMISSION_RESULT_CODE = 0
    }

    private val viewModel by viewModels<RepresentativeViewModel>()
    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val representativesAdapter = RepresentativeListAdapter()
        binding.representativesRecyclerView.adapter = representativesAdapter

        viewModel.representatives.observe(viewLifecycleOwner) {
            it?.let {
                representativesAdapter.submitList(it)
            }
        }

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.getRepresentatives(getAddress())
        }

        binding.buttonLocation.setOnClickListener {
            getLocation()
        }

        return binding.root
    }

    private fun getAddress(): Address {
        val stringArray = resources.getStringArray(R.array.states)
        val itemPos = binding.state.selectedItemPosition

        return Address(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                stringArray[itemPos],
                binding.zip.text.toString()
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle location permission result to get location on permission granted
        if (requestCode == REQUEST_LOCATION_PERMISSION_RESULT_CODE) {
            if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                checkLocationPermissions()
            }
        }

    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            // Request Location permissions
            val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsArray,
                    REQUEST_LOCATION_PERMISSION_RESULT_CODE)
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkLocationPermissions()) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            val locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            super.onLocationResult(locationResult)
                            Log.i(TAG,"onLocationResult called with ${locationResult!!.locations[0]}")
                            val address = geoCodeLocation(locationResult?.locations!!.first())
                            binding.address = address
                            viewModel.getRepresentatives(address)
                        }
                    },
                    Looper.myLooper()
            )
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        var resultAddress = Address("Amphitheatre Parkway", "1600", "Mountain View", "California", "94043")
        try {
            resultAddress = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    .map { address ->
                        Address(address.thoroughfare, address.subThoroughfare, address.subAdminArea, address.adminArea, address.postalCode)
                    }
                    .first()
        } catch (e: Exception) {
            Log.i(TAG, "Geocoder exception: ${e.message}")
        }
        return resultAddress
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}