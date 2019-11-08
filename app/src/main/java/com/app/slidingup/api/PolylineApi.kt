package com.app.slidingup.api

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Url

interface PolylineApi
{
    @GET
    fun getPolylineDataAsync(@Url url : String) : Deferred<JsonObject>
}