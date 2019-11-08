package com.app.slidingup.utils

class Constants {

    companion object {

        const val TIMEOUT_REQUEST : Long = 30

        const val GPS_REQUEST_LOCATION = 1000

        const val REQUEST_DENIED = 403

        const val LANGUAGE_FILTER = "en"

        const val EVENT_TAG : String = "event"

        /* Google direction api Tags */

        const val FORMAT_TAG = "json"

        const val ORIGIN_TAG = "origin="

        const val DESTINATION_TAG = "destination="

        const val SENSOR_TAG = "sensor=false"

        const val KEY_TAG = "key="

        /* Google direction api Response Tags */

        const val STATUS_TAG = "status"

        const val OK_TAG = "OK"

        const val DISTANCE_TAG = "distance"

        const val TEXT_TAG = "text"

        const val ROUTES_TAG = "routes"

        const val LEGS_TAG = "legs"

        const val STEPS_TAG = "steps"

        const val POLYLINE_TAG = "polyline"

        const val POINTS_TAG = "points"

        const val LAT_TAG = "lat"

        const val LNG_TAG = "lng"

        /* Notification */

        const val CHANNEL_ID = "id"

        const val CHANNEL_NAME = "an"

        const val KM = " km"
    }
}