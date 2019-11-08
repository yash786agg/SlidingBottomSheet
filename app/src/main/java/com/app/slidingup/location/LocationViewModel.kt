package com.app.slidingup.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.slidingup.extensions.NonNullMediatorLiveData
import com.app.slidingup.helper.UiHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class LocationViewModel(private val locationProviderClient : FusedLocationProviderClient,
                        private val uiHelper : UiHelper
) : ViewModel() {

    // FOR DATA ---
    private val location = NonNullMediatorLiveData<Location>()
    val currentLocation : LiveData<Location> = location

    /**
     * @return the [LocationResult] with include the Location Lat,Lng
     */
    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.lastLocation.apply { location.postValue(this) }
        }
    }

    /**
     * This function is set to update the user location in a given time of interval.
     */

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        locationProviderClient.requestLocationUpdates(uiHelper.getLocationRequest(), locationCallback, Looper.myLooper())
    }

    /**
     * This function is set to stop the location update
     */

    fun stopLocationUpdates() {
        locationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}