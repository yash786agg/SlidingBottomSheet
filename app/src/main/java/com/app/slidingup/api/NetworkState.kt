package com.app.slidingup.api

sealed class NetworkState<T>(val data: T? = null, val code : Int? = null) {
    class Success<T>(data: T) : NetworkState<T>(data)
    class Loading<T>(data: T? = null) : NetworkState<T>(data)
    class Error<T>(code : Int, data: T? = null) : NetworkState<T>(data, code)
}
