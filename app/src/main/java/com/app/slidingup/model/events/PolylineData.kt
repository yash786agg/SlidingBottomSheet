package com.app.slidingup.model.events

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList
import java.util.HashMap

@Parcelize
data class PolylineData(val routeInfo : RouteInfoData,
                        val ployLineRoutesList : ArrayList<HashMap<String, String>>?) : Parcelable