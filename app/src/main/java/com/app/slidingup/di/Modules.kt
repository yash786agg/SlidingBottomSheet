package com.app.slidingup.di

import android.app.Application
import com.app.slidingup.BuildConfig
import com.app.slidingup.api.EventsApi
import com.app.slidingup.api.PolylineApi
import com.app.slidingup.helper.GoogleMapHelper
import com.app.slidingup.helper.UiHelper
import com.app.slidingup.location.LocationViewModel
import com.app.slidingup.network.createNetworkClient
import com.app.slidingup.repository.events.EventsRepository
import com.app.slidingup.repository.events.EventsUseCase
import com.app.slidingup.repository.events.PolyLineRepository
import com.app.slidingup.repository.events.PolyLineUseCase
import com.app.slidingup.ui.events.viewmodel.EventsViewModel
import com.app.slidingup.ui.events.viewmodel.PolyLineViewModel
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import retrofit2.Retrofit

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        listOf(viewModelModule,
            repositoryModule,
            networkModule,
            uiHelperModule,
            useCaseModule,
            fUsedLocationModule,
            googleMapModule)
    )
}

val viewModelModule = module {
    viewModel { LocationViewModel(locationProviderClient = get(),uiHelper = get()) }
    viewModel { EventsViewModel(eventsUseCase = get()) }
    viewModel { PolyLineViewModel(polyLineUseCase = get()) }
}

val repositoryModule = module {
    single { EventsRepository(eventsApi = get()) }
    single { PolyLineRepository(polylineApi = get()) }
}

val useCaseModule = module {
    single { EventsUseCase(eventsRepository = get()) }
    single { PolyLineUseCase(polyLineRepository = get()) }
}

val networkModule = module {
    single { eventsApi }
    single { polylineApi }
}

val fUsedLocationModule = module {
    single { locationProviderClient(androidApplication()) }
}

val uiHelperModule = module {
    single { UiHelper(androidContext()) }
}

val googleMapModule = module {
    single { GoogleMapHelper() }
}

private val retrofit : Retrofit = createNetworkClient(BuildConfig.BASE_URL)

private val eventsApi : EventsApi = retrofit.create(EventsApi::class.java)

private val polylineApi : PolylineApi = retrofit.create(PolylineApi::class.java)

private fun locationProviderClient(androidApplication : Application)
        = LocationServices.getFusedLocationProviderClient(androidApplication)
