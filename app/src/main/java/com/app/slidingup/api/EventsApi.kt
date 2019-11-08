package com.app.slidingup.api

import com.app.slidingup.model.events.EventsApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface EventsApi {

    @GET("events/")
    fun getEventsAsync(@Query("language_filter") language : String) : Deferred<EventsApiResponse>
}