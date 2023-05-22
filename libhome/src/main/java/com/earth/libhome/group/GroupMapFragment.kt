package com.earth.libhome.group

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.*
import com.earth.libbase.dialog.LocationDialog
import com.earth.libbase.network.RetrofitManager
import com.earth.libbase.util.BaseDateUtils
import com.earth.libhome.R
import com.earth.libhome.databinding.FragmentGroupmapBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonIOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class GroupMapFragment : BaseFragment<FragmentGroupmapBinding>(), OnMapReadyCallback {

    private val mGroupApiModle by viewModel<GroupApiModle>()

    private val mGroupModle: GroupModle by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)


    private var map: GoogleMap? = null
    private var locationPermissionGranted: Boolean = true
    private var code = -1
    private var result: ArrayList<Address> = ArrayList()
    private var viewFlag: Boolean? = true
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun getLayoutId(): Int = R.layout.fragment_groupmap

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        //地图初始化
        val mapFragment: SupportMapFragment? =
            childFragmentManager!!.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        code = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(requireContext())
        if (code == ConnectionResult.SUCCESS) {
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
        }
        mBinding?.run {
            setViewActionBarHight(tvLibHomeLeftTool)
            tvLibHomeLeftTool.setOnClickListener {
                findNavController().popBackStack()
            }
            LibMapDone.isEnabled = false
            LibMapDone.setOnClickListener {
                var address = LibLocation.text.toString().trim()
                mGroupModle.setAddress(address)
                if (mGroupModle.getData().latitude == null || mGroupModle.getData().longitude == null) {
                    requireActivity().longToast(getString(R.string.label_Location_failed))
                    return@setOnClickListener
                }
                val blockDialog = LocationDialog(
                    mGroupModle.getData(),
                    onBlockClick = {
                        concernedAdd()
                    })
                blockDialog.show(requireActivity().supportFragmentManager, "LocationDialog")
            }
            LibLocation.run {
                doAfterTextChanged {
                    checkCanUpdate()
                }
            }
        }
        getPermissions()
    }

    @SuppressLint("MissingPermission")
    private fun getPermissions() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionsList.add(1, Manifest.permission.ACCESS_FINE_LOCATION)
        delayLaunch(500) {
            block = {
                requestPermissions {
                    permissions = permissionsList
                    onAllPermissionsGranted = {
                        if (code == ConnectionResult.SUCCESS) {
                            fusedLocationClient?.lastLocation
                                .addOnSuccessListener { location: Location? ->
                                    // Got last known location. In some rare situations this can be null.
                                    launch {
                                        if (location != null) {
                                            val gc = Geocoder(
                                                requireContext(),
                                                Locale.getDefault()
                                            )
                                            mGroupModle.setLocation(
                                                location.latitude,
                                                location.longitude
                                            )
                                            gc.getFromLocation(
                                                location.latitude,
                                                location.longitude, 1
                                            ).let {
                                                result.clear()
                                                result.addAll(it)
                                                withContext(Dispatchers.Main) {
                                                    if (result.size > 0) {
                                                        result[0].let { itAddre ->
                                                            if (itAddre.adminArea != null && itAddre.locality != null && itAddre.subLocality != null) {
                                                                var locationStr =
                                                                    itAddre.subLocality + "，" + itAddre.locality + "，" + itAddre.adminArea
                                                                var address =
                                                                    itAddre.getAddressLine(0)
                                                                        .toString()
                                                                address?.let {
                                                                    mBinding?.LibLocation?.setText(
                                                                        address
                                                                    )
                                                                }
                                                                mGroupModle.setAddress(locationStr)
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                        }
                    }
                    onPermissionsDenied = {
                    }
                    onPermissionsNeverAsked = {
                    }
                }
            }
        }
    }


    private fun concernedAdd() {
        launch {
            mGroupApiModle.communityCreate(mGroupModle.getData())
                .onStart {
                }.catch {
                    activity?.toast("Only one community can be created")
                }.onCompletion {
                }.collectLatest {
                    it.data?.let {
                        ARouter.getInstance()
                            .build(Constance.GroupMainActivityURL)
                            .withString("GroupID", it)
                            .navigation()
                    }
                    MessageObservable.messageObservable.newMessage(
                        UpdateEntity(
                            groupList = UpdateEntity.GROUP
                        )
                    )
                    activity?.finish()
                }
        }
    }

    private fun jump() {
        if (BaseDateUtils.isFastClick()) {
            ARouter.getInstance()
                .build(Constance.GroupMainActivityURL)
                .navigation()
        }
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            // Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient?.lastLocation
                locationResult?.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
        }
    }

    override fun onMapReady(map: GoogleMap) {

        this.map = map
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }

    private fun checkCanUpdate() {
        mBinding?.LibMapDone?.isEnabled = when {
            mBinding?.LibLocation?.text.isNullOrEmpty() -> {
                false
            }
            mBinding?.LibLocation?.text?.toString()!!.length > 50 -> {
                false
            }

            else -> {
                true
            }
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 15

    }

}