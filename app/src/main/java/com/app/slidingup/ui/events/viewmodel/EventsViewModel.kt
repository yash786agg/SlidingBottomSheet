package com.app.slidingup.ui.events.viewmodel

import com.app.slidingup.api.NetworkState
import com.app.slidingup.base.BaseViewModel
import com.app.slidingup.extensions.NonNullMediatorLiveData
import com.app.slidingup.model.events.EventsApiResponse
import com.app.slidingup.repository.events.EventsUseCase
import kotlinx.coroutines.launch

class EventsViewModel(private val eventsUseCase : EventsUseCase) : BaseViewModel() {

    // FOR DATA --
    fun getEvents() : NonNullMediatorLiveData<NetworkState<EventsApiResponse>> {
        val events = NonNullMediatorLiveData<NetworkState<EventsApiResponse>>()

        ioScope.launch { eventsUseCase.executeQuery(events) }

        return events
    }
}