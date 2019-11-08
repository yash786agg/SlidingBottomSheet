package com.app.slidingup.ui.events.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.slidingup.R
import com.app.slidingup.databinding.FragmentEventsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Double.parseDouble
import java.util.ArrayList
import kotlin.math.roundToInt
import android.text.TextUtils
import com.app.slidingup.api.NetworkState
import com.app.slidingup.extensions.nonNull
import com.app.slidingup.helper.GoogleMapHelper
import com.app.slidingup.helper.UiHelper
import com.app.slidingup.ui.events.adapter.EventsImagesAdapter
import com.app.slidingup.ui.events.viewmodel.PolyLineViewModel
import com.app.slidingup.model.events.Event
import com.app.slidingup.model.events.EventImages
import com.app.slidingup.model.events.PolylineData
import com.app.slidingup.utils.Constants.Companion.EVENT_TAG
import com.app.slidingup.utils.Constants.Companion.KM
import com.app.slidingup.utils.Constants.Companion.LAT_TAG
import com.app.slidingup.utils.Constants.Companion.LNG_TAG
import com.app.slidingup.utils.Constants.Companion.REQUEST_DENIED

class EventsFragment(private val googleMap : GoogleMap, private val markerLatLng : LatLng
                     , private val currentLatLng : LatLng) : Fragment() {

    private val uiHelper : UiHelper by inject()
    private val googleMapHelper : GoogleMapHelper by inject()
    private val polyLineVM : PolyLineViewModel by viewModel()
    private val listLatLng = ArrayList<LatLng>()
    private var binding : FragmentEventsBinding? = null
    private var bottomSheetBehavior : BottomSheetBehavior<View>? = null
    private val eventsImagesAdapter = EventsImagesAdapter()
    private var events : Event? = null
    private var bottomSheet : LinearLayout? = null
    private var recyclerView : RecyclerView? = null
    private var eventsPB : ProgressBar? = null
    private var blackPolyLine : Polyline? = null
    private var greyPolyLine : Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        events = arguments?.getParcelable(EVENT_TAG)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        binding?.lifecycleOwner = this
        binding?.event = events

        val view = binding?.root

        // find container view
        bottomSheet = view?.findViewById(R.id.bottom_sheet)

        recyclerView = view?.findViewById(R.id.recyclerView) as RecyclerView

        eventsPB = view.findViewById(R.id.events_pb) as ProgressBar

        initializeBottomSheet()

        initRecyclerView()

        drawPloyLine()
        return view
    }

    private fun drawPloyLine() {
        if(currentLatLng != markerLatLng) {
            clearPloyLineAnimation()

            if(uiHelper.getConnectivityStatus())
                subscribePolyLineObserver(markerLatLng,currentLatLng)
            else
                uiHelper.toast(resources.getString(R.string.error_network_connection))
        }
    }

    // Init the bottom sheet behavior
    private fun initializeBottomSheet()
    {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        if(events != null) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            bottomSheetBehavior?.setBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val h = bottomSheet.height.toFloat()
                    val off = h * slideOffset

                    when (bottomSheetBehavior?.state) {
                        BottomSheetBehavior.STATE_DRAGGING -> moveMarkerToCenter(off)
                        BottomSheetBehavior.STATE_SETTLING -> moveMarkerToCenter(off)
                    }
                }
                override fun onStateChanged(bottomSheet: View, newState: Int) {}
            })
        }
        else {
            clearPloyLineAnimation()
            bottomSheetBehavior?.peekHeight = 0
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    //Setup the adapter class for the RecyclerView
    private fun initRecyclerView() {
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = eventsImagesAdapter
        if(events?.eventDescription?.eventImages?.size!! >= 1) {
            recyclerView?.visibility = View.VISIBLE
            eventsImagesAdapter.updateData(events?.eventDescription?.eventImages as ArrayList<EventImages>)
        }
        else recyclerView?.visibility = View.GONE
    }

    // Reposition marker at the center
    private fun moveMarkerToCenter(off: Float) {
        googleMap.setPadding(0, 0, 0, (off * 1.0f).roundToInt())
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng))
    }

    // Removing the PloyLine Animation
    private fun clearPloyLineAnimation()
    {
        if(blackPolyLine != null && greyPolyLine != null) {
            blackPolyLine?.remove()
            greyPolyLine?.remove()
            this.listLatLng.clear()
        }
    }

    // Subscribe the PolyLine Observer to get the Route info using LatLng
    private fun subscribePolyLineObserver(markerLatLng: LatLng, currentLatLng: LatLng)
    {
        // OBSERVABLES ---
        polyLineVM.getPolyLineData(markerLatLng,currentLatLng).nonNull().observe(this, Observer {
            when(it) {
                is NetworkState.Loading -> eventsPB?.let { data -> uiHelper.showProgressBar(data,true) }
                is NetworkState.Success -> {
                    if(it.data != null) {
                        eventsPB?.let { data -> uiHelper.showProgressBar(data,false) }
                        drawPolyline(it.data)
                    }
                }
                is NetworkState.Error -> {
                    eventsPB?.let { data -> uiHelper.showProgressBar(data,false) }
                    if(it.code == REQUEST_DENIED)
                        uiHelper.toast(resources.getString(R.string.error_getting_route))
                }
            }
        })
    }

    private fun drawPolyline(result : List<PolylineData>) {
        val points : ArrayList<LatLng> = ArrayList()

        // Traversing through all the routes
        for (i in result.indices)
        {
            val routeInfoData = result[i].routeInfo
            val distance : String = routeInfoData.distance.replace(KM,"")
            if(distance.toFloat() <= 4.9)
                createNotification(distance)

            // Fetching i-th route
            val path = result[i].ployLineRoutesList

            // Fetching all the points in i-th route
            for (j in path?.indices!!) {
                val point = path[j]

                val lat = point[LAT_TAG]?.let { parseDouble(it) }
                val lng = point[LNG_TAG]?.let { parseDouble(it) }
                val position = lat?.let { lng?.let { data -> LatLng(it, data) } }

                position?.let { points.add(it) }
            }

            this.listLatLng.addAll(points)
        }

        blackPolyLine = googleMap.addPolyline(googleMapHelper.getPolyLineOptions(Color.BLACK))
        greyPolyLine = googleMap.addPolyline(googleMapHelper.getPolyLineOptions(Color.GRAY))

        if(blackPolyLine != null && greyPolyLine != null && listLatLng.size >= 1)
            googleMapHelper.animatePolyLine(blackPolyLine,greyPolyLine,listLatLng)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearPloyLineAnimation()
    }

    private fun createNotification(distance : String) {

        var eventName = ""
        if(!TextUtils.isEmpty(events?.eventName?.enName))
            eventName = events?.eventName?.enName.toString()
        else if(!TextUtils.isEmpty(events?.eventName?.fiName))
            eventName = events?.eventName?.fiName.toString()
        else if(!TextUtils.isEmpty(events?.eventName?.svName))
            eventName = events?.eventName?.svName.toString()
        else if(!TextUtils.isEmpty(events?.eventName?.zhName))
            eventName = events?.eventName?.zhName.toString()


        val message : String = activity?.resources?.getString(R.string.info_event_name)+" "+
                eventName+" "+activity?.resources?.getString(R.string.info_event_happening)+" "+distance + KM
        uiHelper.showNotification(message)
    }
}