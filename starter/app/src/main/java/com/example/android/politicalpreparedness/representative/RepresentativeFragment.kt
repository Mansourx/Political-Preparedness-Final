package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.gms.location.LocationServices
import java.util.*

class DetailFragment : Fragment() {

    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }

    private lateinit var binding: FragmentRepresentativeBinding

    private var mDataset: ArrayList<Representative>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_representative,
            container, false
        )

        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val repAdapter = RepresentativeListAdapter()
        val spinAdapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, resources.getStringArray(R.array.states)
        )
        binding.representativeList.adapter = repAdapter
        binding.state.adapter = spinAdapter

        observeRepresentatives(repAdapter)

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.getAddressFromLines(binding.state.selectedItem as String)
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            getLocation()
        }

        savedInstanceState?.apply {
            getParcelableArrayList<Parcelable>(KEY_SAVED_REPRESENTATIVE_DATASET)?.let {
                mDataset = it as ArrayList<Representative>
            }
            getParcelable<Address>(KEY_ADDRESS)?.let {
                viewModel.getAddressFromLocation(it)
            }
            getInt(KEY_MOTION_LAYOUT).let {
                binding.representativeContainer.transitionToState(it)
            }
            getParcelable<Parcelable>(KEY_REPRESENTATIVE_LIST)?.let {
                binding.representativeList.layoutManager?.onRestoreInstanceState(it)
            }
        }
        if (savedInstanceState == null) observeAddress() else repAdapter.submitList(mDataset);


        return binding.root
    }

    private fun checkConnection(): Boolean {
        val connectivityManager = context?.getSystemService(ConnectivityManager::class.java)
        val wifiConnection = connectivityManager?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileDataConnection =
            connectivityManager?.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return if (wifiConnection?.isConnectedOrConnecting == true) {
            true
        } else {
            val msg = getString(R.string.no_internet_connection_msg)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            mobileDataConnection?.isConnectedOrConnecting == true
        }
    }

    private fun observeAddress() {
        viewModel.address.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.getRepresentatives(it.toFormattedString())
            }
        })
    }

    private fun observeRepresentatives(repAdapter: RepresentativeListAdapter) {
        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            it?.let {
                mDataset = it as ArrayList
                repAdapter.submitList(mDataset)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val listState = binding.representativeList.layoutManager?.onSaveInstanceState()
        outState.putInt(KEY_MOTION_LAYOUT, binding.representativeContainer.currentState)
        outState.putParcelable(KEY_REPRESENTATIVE_LIST, listState)
        outState.putParcelableArrayList(KEY_SAVED_REPRESENTATIVE_DATASET, mDataset)
        outState.putParcelable(KEY_ADDRESS, binding.viewModel?.address?.value)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkConnection() && checkLocationPermissions()) {
            LocationServices.getFusedLocationProviderClient(requireContext())
                .lastLocation
                .addOnSuccessListener {
                    it?.let {
                        viewModel.getAddressFromLocation(geoCodeLocation(it))
                    }
                }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare ?: "",
                    address.subThoroughfare ?: "",
                    address.locality ?: "",
                    address.adminArea ?: "",
                    address.postalCode ?: ""
                )
            }
            .first()
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
        const val KEY_ADDRESS = "address"
        const val KEY_MOTION_LAYOUT = "motionLayout"
        const val KEY_REPRESENTATIVE_LIST = "representative_list"
        const val KEY_SAVED_REPRESENTATIVE_DATASET = "save representative dataset"
    }

}