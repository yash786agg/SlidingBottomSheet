package com.app.slidingup.ui.events.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.app.slidingup.R
import com.app.slidingup.api.NetworkState
import com.app.slidingup.extensions.GpsEnableListener
import com.app.slidingup.extensions.nonNull
import com.app.slidingup.helper.GoogleMapHelper
import com.app.slidingup.helper.PermissionHelper
import com.app.slidingup.helper.PermissionHelper.Companion.ACCESS_FINE_LOCATION
import com.app.slidingup.helper.PermissionHelper.Companion.PERMISSIONS_REQUEST_LOCATION
import com.app.slidingup.helper.UiHelper
import com.app.slidingup.location.GpsSetting
import com.app.slidingup.location.LocationViewModel
import com.app.slidingup.ui.events.fragment.EventsFragment
import com.app.slidingup.ui.events.viewmodel.EventsViewModel
import com.app.slidingup.utils.Constants.Companion.EVENT_TAG
import com.app.slidingup.utils.Constants.Companion.GPS_REQUEST_LOCATION
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_events.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EventsActivity : AppCompatActivity(), OnMapReadyCallback, PermissionHelper.OnPermissionRequested
    ,GoogleMap.OnMarkerClickListener {

    // FOR DATA ---
    private val locationVM : LocationViewModel by viewModel()
    private val eventsVM : EventsViewModel by viewModel()
    private val uiHelper : UiHelper by inject()
    private val googleMapHelper : GoogleMapHelper by inject()
    private var gpsSetting : GpsSetting? = null
    private var permissionHelper : PermissionHelper? = null
    private var googleMap : GoogleMap? = null
    private var currentLatLng : LatLng? = null
    private var isPermissionPermanentlyDenied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPlayServicesAvailable()

        gpsSetting = GpsSetting(this,uiHelper)

        permissionHelper = PermissionHelper(this,uiHelper)

        if(!permissionHelper?.isPermissionGranted(ACCESS_FINE_LOCATION)!!)
            permissionHelper?.requestPermission(arrayOf(ACCESS_FINE_LOCATION),PERMISSIONS_REQUEST_LOCATION,this)
        else enableGps()
    }

    /*
     * Checking out is Google Play Services app is installed or not.
     * */

    private fun checkPlayServicesAvailable() {
        if(!uiHelper.isPlayServicesAvailable()) {
            uiHelper.toast(resources.getString(R.string.play_service_not_installed))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if(isPermissionPermanentlyDenied) checkPermissionGranted()
    }

    /*
     * Checking whether Location Permission is granted or not.
     * */

    private fun checkPermissionGranted() {
        if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionHelper?.openSettingsDialog()
        else enableGps()
    }

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray) {
        permissionHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * This function is to get the result form [PermissionHelper] class
     *
     * @param isPermissionGranted the [Boolean]
     */

    override fun onPermissionResponse(isPermissionGranted : Boolean) {

        if(!isPermissionGranted) isPermissionPermanentlyDenied = true
        else enableGps()
    }

    private fun enableGps() {
        isPermissionPermanentlyDenied = false

        if (!uiHelper.isLocationProviderEnabled()) subscribeLocationObserver()
        else gpsSetting?.openGpsSettingDialog()
    }

    // Start Observing the User Current Location and set the marker to it.
    private fun subscribeLocationObserver()
    {
        uiHelper.showProgressBar(progress_bar,true)

        // OBSERVABLES ---
        locationVM.currentLocation.nonNull().observe(this, Observer {

            uiHelper.showProgressBar(progress_bar,false)
            currentLatLng = googleMapHelper.getLatLng(it.latitude,it.longitude)

            currentLatLng?.let{ data -> mapSetUp(data) }

            locationVM.stopLocationUpdates()
        })

        locationVM.requestLocationUpdates()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GPS_REQUEST_LOCATION ->
                when (resultCode) {
                    RESULT_OK -> subscribeLocationObserver()

                    RESULT_CANCELED -> {
                        uiHelper.showPositiveDialogWithListener(this,
                            resources.getString(R.string.need_location),
                            resources.getString(R.string.location_content),
                            object : GpsEnableListener {
                                override fun onPositive() {
                                    enableGps()
                                }
                            }, resources.getString(R.string.turn_on), false)
                    }
                }
        }
    }

    // Add a marker to the current Location, and move the camera.
    private fun mapSetUp(latLing : LatLng?) {
        if(googleMap != null) {
            googleMap?.addMarker(latLing?.let { googleMapHelper.addCurrentLocationMarker(it) })
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLing, 14.0F))

            if(uiHelper.getConnectivityStatus()) {
                // OBSERVABLES ---
                eventsVM.getEvents().nonNull().observe(this,Observer{
                    when(it)
                    {
                        is NetworkState.Loading ->  uiHelper.showProgressBar(progress_bar,true)

                        is NetworkState.Success -> {
                            if(it.data != null) {
                                uiHelper.showProgressBar(progress_bar,false)

                                for (i in it.data.events.indices)
                                    googleMap?.addMarker(it.data.events[i].eventLocation?.let { data ->
                                        googleMapHelper.addMarker(data)})?.tag = it.data.events[i]
                            }
                        }
                        is NetworkState.Error -> uiHelper.showProgressBar(progress_bar,false)
                    }
                })
            }
            else uiHelper.showSnackBar(event_activity_rv,resources.getString(R.string.error_network_connection))
        }
    }

    /** Called when the map is ready. */
    override fun onMapReady(map : GoogleMap?) {
        googleMap = map
        googleMap?.let { googleMapHelper.defaultMapSettings(it) }
        googleMap?.setOnMarkerClickListener(this)
    }

    /** Called when the user clicks a marker. */
    override fun onMarkerClick(marker : Marker?) : Boolean {

        val events = marker?.tag
        events?.let {
            googleMapHelper.changeMarkerColor(marker)

            val fragment = googleMap?.let { currentLatLng?.let { data -> EventsFragment(it, marker.position, data) } }
            val args = Bundle()
            args.putParcelable(EVENT_TAG, events as Parcelable?)
            fragment?.arguments = args

            fragment?.let { supportFragmentManager.beginTransaction().replace(R.id.events_fragment_container, it).commit() }
        }
        return false
    }
}