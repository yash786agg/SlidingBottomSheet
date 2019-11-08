package com.app.slidingup.ui.events.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.slidingup.R
import com.app.slidingup.databinding.AdapterImageEventsBinding
import com.app.slidingup.model.events.EventImages
import java.util.ArrayList

class EventsImagesAdapter : RecyclerView.Adapter<EventsImagesAdapter.MyViewHolder>() {
    private var eventItemList : ArrayList<EventImages> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : AdapterImageEventsBinding = DataBindingUtil
            .inflate(LayoutInflater.from(parent.context), R.layout.adapter_image_events, parent, false)

        return MyViewHolder(binding)
    }

    // Binds each data in the ArrayList to a view
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataModel = eventItemList[position]
        holder.dataItemBinding.dataManager = dataModel
    }

    // Gets the number of Items in the list
    override fun getItemCount(): Int = eventItemList.size

    override fun getItemViewType(position: Int): Int = position

    // UpdateData method is to add items in the eventItemList and notify the adapter for the data change.
    fun updateData(eventItemList : ArrayList<EventImages>)
    {
        this.eventItemList.clear()
        this.eventItemList = eventItemList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(dataItemLayoutBinding : AdapterImageEventsBinding) :
        RecyclerView.ViewHolder(dataItemLayoutBinding.root) {
        var dataItemBinding : AdapterImageEventsBinding = dataItemLayoutBinding

        init { dataItemLayoutBinding.root.tag = dataItemLayoutBinding.root }
    }
}