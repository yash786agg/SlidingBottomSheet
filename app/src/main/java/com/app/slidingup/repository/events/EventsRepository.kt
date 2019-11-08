package com.app.slidingup.repository.events

import com.app.slidingup.api.EventsApi
import com.app.slidingup.utils.Constants.Companion.LANGUAGE_FILTER

class EventsRepository(private val eventsApi : EventsApi) {

    suspend fun getEvents() = eventsApi.getEventsAsync(LANGUAGE_FILTER).await()
}