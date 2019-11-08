package com.app.slidingup.model.events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventDescription(@SerializedName("intro") val eventIntro : String?,
                            @SerializedName("images") val eventImages : List<EventImages>?) : Parcelable