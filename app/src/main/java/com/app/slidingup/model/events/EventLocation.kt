package com.app.slidingup.model.events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventLocation(@SerializedName("lat") val latitude : Double?,
                         @SerializedName("lon") val longitude : Double?) : Parcelable