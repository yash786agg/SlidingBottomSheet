package com.app.slidingup.helper

import android.app.*
import android.content.Context
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.app.slidingup.R
import com.app.slidingup.extensions.GpsEnableListener
import com.app.slidingup.utils.Constants.Companion.CHANNEL_ID
import com.app.slidingup.utils.Constants.Companion.CHANNEL_NAME
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationRequest
import com.google.android.material.snackbar.Snackbar

class UiHelper(private val context : Context)
{
    fun getConnectivityStatus() : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isPlayServicesAvailable() : Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return ConnectionResult.SUCCESS == status
    }

    fun toast(content: String) = Toast.makeText(context, content, Toast.LENGTH_LONG).show()

    fun showSnackBar(view: View, content: String) = Snackbar.make(view, content, Snackbar.LENGTH_LONG).show()

    fun getLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 3000
        return locationRequest
    }

    fun isLocationProviderEnabled() : Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }


    fun showPositiveDialogWithListener(activity: Activity, title: String, content: String,
                                       listener: GpsEnableListener,
                                       positiveText: String, cancelable: Boolean)
    {
        val builder = AlertDialog.Builder(activity, R.style.MyAlertDialogStyle)
        builder.setTitle(title)
        builder.setMessage(content)
        builder.setCancelable(cancelable)
        builder.setPositiveButton(positiveText){ dialog, _ ->
            listener.onPositive()
            dialog.dismiss()
        }

        val alert = builder.create()

        if(!alert.isShowing)
            alert.show()
    }

    fun showProgressBar(progressBar : ProgressBar, display : Boolean)
    {
        if(!display) progressBar.visibility = View.GONE
        else progressBar.visibility = View.VISIBLE
    }

    fun showNotification(message : String) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.setSound(null, null)
            notificationChannel.enableLights(false)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(context,CHANNEL_ID)

        builder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.resources.getString(R.string.info_event_alert))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)

        notificationManager.notify(0, builder.build())
    }
}