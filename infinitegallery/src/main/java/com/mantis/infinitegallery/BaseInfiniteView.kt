package com.mantis.infinitegallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View

open abstract class BaseInfiniteView<T>(
    private val context: Context,
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)) {


    /**create and bind data to view here
     *
     */
    abstract fun bindView(obj:T): View


    /**
     * you should use this method to inflate view
     */
    fun inflateView(layoutId: Int): View {
        return layoutInflater.inflate(layoutId, null)
    }


}