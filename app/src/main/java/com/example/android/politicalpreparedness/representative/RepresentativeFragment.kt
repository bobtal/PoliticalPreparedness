package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.setNewValue
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        //COMPLETE: Add Constant for Location request
        const val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var permissionSnackbar: Snackbar

    //COMPLETE: Declare ViewModel
    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //COMPLETE: Establish bindings
        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //COMPLETE: Define and assign Representative adapter
        val representativeAdapter = RepresentativeListAdapter()
        binding.representativesRecycler.adapter = representativeAdapter

        //COMPLETE: Populate Representative adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            it?.let {
                representativeAdapter.submitList(it)
            }
        })

        // Observe the address so that we can populate the spinner with the state
        viewModel.address.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.state.setNewValue(it.state)
            }
        })

        //COMPLETE: Establish button listeners for field and location search
        binding.buttonLocation.setOnClickListener {
            getLocation()
        }

        binding.buttonSearch.setOnClickListener {
            viewModel.getRepresentatives(viewModel.address.value.toString())
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //COMPLETE: Handle location permission result to get location on permission granted
        if (requestCode == REQUEST_LOCATION_PERMISSION) {

        } else {
            showSnackbar()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //COMPLETE: Request Location permissions
                // requesting it in getLocation
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //COMPLETE: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //COMPLETE: Get location from LocationServices
        if (checkLocationPermissions()) {
            //COMPLETE: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
            LocationServices.getFusedLocationProviderClient(requireContext())
                    .lastLocation.addOnSuccessListener { location ->
                        viewModel.setAddressFromGeoLocation(geoCodeLocation(location))
                    }
        }
        else {
            // Request location permission here instead of in checkLocationPermissions
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun showSnackbar() {
        permissionSnackbar = Snackbar.make(
                activity?.findViewById(R.id.representative_motion_layout) as View,
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE
        )
        permissionSnackbar.setAction(R.string.retry, View.OnClickListener {
            checkLocationPermissions()
        })
        permissionSnackbar.show()
    }

}