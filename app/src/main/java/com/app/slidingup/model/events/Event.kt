package com.app.slidingup.model.events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(@SerializedName("id") val eventId : String?,
                 @SerializedName("name") val eventName : EventName?,
                 @SerializedName("location") val eventLocation : EventLocation?,
                 @SerializedName("description") val eventDescription : EventDescription?) : Parcelable