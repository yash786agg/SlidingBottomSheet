package com.app.slidingup.repository.events

import androidx.lifecycle.MediatorLiveData
import com.app.slidingup.api.NetworkState
import com.app.slidingup.extensions.NonNullMediatorLiveData
import com.app.slidingup.model.events.EventsApiResponse
import retrofit2.HttpException

class EventsUseCase(private val eventsRepository : EventsRepository)
{
    suspend fun executeQuery(events : NonNullMediatorLiveData<NetworkState<EventsApiResponse>>)
            : MediatorLiveData<NetworkState<EventsApiResponse>> {

        events.postValue(NetworkState.Loading())
        try
        {
            val response = eventsRepository.getEvents()
            events.postValue(NetworkState.Success(response))
        }
        catch (exception : HttpException) {
            events.postValue(NetworkState.Error(exception.code(),null))
        }

        return events
    }
}