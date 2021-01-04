package com.yasincidemcarto.androidtest.core

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object Bindings {
    @JvmStatic
    @BindingAdapter("src_bind")
    fun setImageUrl(view: ImageView, url: String?) {
        if (url != null) Picasso.get().load(url).into(view)
    }

}
