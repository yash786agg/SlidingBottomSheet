package com.app.slidingup.repository.events

import com.app.slidingup.BuildConfig.DIRECTION_API_URL
import com.app.slidingup.BuildConfig.MAP_API_kEY
import com.app.slidingup.api.NetworkState
import com.app.slidingup.extensions.NonNullMediatorLiveData
import com.app.slidingup.model.events.PolylineData
import com.app.slidingup.model.events.RouteInfoData
import com.app.slidingup.utils.Constants.Companion.DESTINATION_TAG
import com.app.slidingup.utils.Constants.Companion.DISTANCE_TAG
import com.app.slidingup.utils.Constants.Companion.FORMAT_TAG
import com.app.slidingup.utils.Constants.Companion.KEY_TAG
import com.app.slidingup.utils.Constants.Companion.LAT_TAG
import com.app.slidingup.utils.Constants.Companion.LEGS_TAG
import com.app.slidingup.utils.Constants.Companion.LNG_TAG
import com.app.slidingup.utils.Constants.Companion.OK_TAG
import com.app.slidingup.utils.Constants.Companion.ORIGIN_TAG
import com.app.slidingup.utils.Constants.Companion.POINTS_TAG
import com.app.slidingup.utils.Constants.Companion.POLYLINE_TAG
import com.app.slidingup.utils.Constants.Companion.REQUEST_DENIED
import com.app.slidingup.utils.Constants.Companion.ROUTES_TAG
import com.app.slidingup.utils.Constants.Companion.SENSOR_TAG
import com.app.slidingup.utils.Constants.Companion.STATUS_TAG
import com.app.slidingup.utils.Constants.Companion.STEPS_TAG
import com.app.slidingup.utils.Constants.Companion.TEXT_TAG
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.util.ArrayList
import java.util.HashMap

class PolyLineUseCase(private val polyLineRepository : PolyLineRepository) {

    suspend fun executeQuery(data : NonNullMediatorLiveData<NetworkState<List<PolylineData>>>,
                             markerLatLng : LatLng, currentLatLng : LatLng)
            : NonNullMediatorLiveData<NetworkState<List<PolylineData>>> {

        data.postValue(NetworkState.Loading())

        // Origin of route
        val strOrigin = ORIGIN_TAG + markerLatLng.latitude + "," + markerLatLng.longitude

        // Destination of route
        val strDest = DESTINATION_TAG + currentLatLng.latitude + "," + currentLatLng.longitude

        val key = KEY_TAG+""+MAP_API_kEY

        // Building the parameters to the web service
        val parameters = "$strOrigin&$strDest&$SENSOR_TAG&$key"

        // Building the url to the web service
        val directionApiUrl = "$DIRECTION_API_URL$FORMAT_TAG?$parameters"

        try {
            val response = polyLineRepository.getPolyLineData(directionApiUrl)

            val gsonData : JsonObject = JsonParser().parse(response.toString()).asJsonObject

            if(gsonData.get(STATUS_TAG).asString == OK_TAG)
                data.postValue(NetworkState.Success(parseJsonData(JSONObject(gsonData.toString()))))
            else data.postValue(NetworkState.Error(REQUEST_DENIED))
        }
        catch (e: HttpException) {
            e.printStackTrace()
        }

        return data
    }

    private fun parseJsonData(jObject: JSONObject) : List<PolylineData> {

        val routes = ArrayList<PolylineData>()
        val jRoutes: JSONArray
        var jLegs: JSONArray
        var jSteps: JSONArray

        try
        {
            jRoutes = jObject.getJSONArray(ROUTES_TAG)

            /** Traversing all routes  */
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes.get(i) as JSONObject).getJSONArray(LEGS_TAG)

                val path = ArrayList<HashMap<String, String>>()

                /** Traversing all legs  */
                for (j in 0 until jLegs.length())
                {
                    val distance = (jLegs.get(j) as JSONObject).getJSONObject(DISTANCE_TAG).getString(TEXT_TAG)

                    val routeInfoData = RouteInfoData(distance)

                    jSteps = (jLegs.get(j) as JSONObject).getJSONArray(STEPS_TAG)

                    /** Traversing all steps  */
                    for (k in 0 until jSteps.length()) {
                        val polyline = ((jSteps.get(k) as JSONObject).get(POLYLINE_TAG) as JSONObject).get(POINTS_TAG) as String
                        val list = decodePoly(polyline)

                        /** Traversing all points  */
                        for (l in list.indices) {
                            val hm = HashMap<String, String>()
                            hm[LAT_TAG] = list[l].latitude.toString()
                            hm[LNG_TAG] = list[l].longitude.toString()
                            path.add(hm)
                        }
                    }

                    val polylineData = PolylineData(routeInfoData,path)

                    routes.add(polylineData)
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

        return routes
    }

    private fun decodePoly(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }
}