package com.app.slidingup.network

import com.app.slidingup.utils.Constants.Companion.TIMEOUT_REQUEST
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun createNetworkClient(baseUrl: String) = retrofitClient(baseUrl, httpClient())

private fun httpClient(): OkHttpClient =
    OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_REQUEST, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_REQUEST, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_REQUEST, TimeUnit.SECONDS).build()

private fun retrofitClient(baseUrl: String, httpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
