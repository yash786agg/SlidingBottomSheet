package com.app.slidingup.repository.events

import com.app.slidingup.api.PolylineApi

class PolyLineRepository(private val polylineApi : PolylineApi)
{
    suspend fun getPolyLineData(url : String) = polylineApi.getPolylineDataAsync(url).await()
}