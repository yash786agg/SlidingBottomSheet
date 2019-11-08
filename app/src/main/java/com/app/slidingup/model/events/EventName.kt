package com.app.slidingup.model.events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventName(@SerializedName("fi") val fiName : String?,
                     @SerializedName("en") val enName : String?,
                     @SerializedName("sv") val svName : String?,
                     @SerializedName("zh") val zhName : String?) : Parcelable