package com.app.slidingup.model.events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventsApiResponse(@SerializedName("data") val events : List<Event>) : Parcelable