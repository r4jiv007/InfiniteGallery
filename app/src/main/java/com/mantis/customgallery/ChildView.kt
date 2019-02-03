package com.mantis.customgallery

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mantis.infinitegallery.BaseInfiniteView

class ChildView(context: Context) : BaseInfiniteView<Image>(context) {
    override fun bindView(image :Image): View {
        val view = inflateView(R.layout.item_view)
        val imageView = view.findViewById<ImageView>(R.id.ivImage)
        Glide.with(imageView.context).load(image.url).into(imageView)
        return view
    }
}