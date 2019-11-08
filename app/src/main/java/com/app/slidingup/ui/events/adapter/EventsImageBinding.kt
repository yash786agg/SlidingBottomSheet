package com.app.slidingup.ui.events.adapter

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.api.load
import coil.request.CachePolicy
import com.app.slidingup.R

@BindingAdapter(value = ["eventItemImage"])
fun loadImage(view: ImageView, imageUrl: String?) {
    if(!TextUtils.isEmpty(imageUrl))
        view.load(imageUrl) {
            crossfade(true)
            placeholder(R.drawable.place_holder)
            error(R.drawable.place_holder)
            memoryCachePolicy(CachePolicy.DISABLED)
        }
}
