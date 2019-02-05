package com.mantis.infinitegallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View

open abstract class BaseInfiniteView(
    private val context: Context,
    private val item:Any,
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)) {


    /**create and bind data to view here
     *
     */
    abstract fun bindView(): View


    /**
     * you should use this method to inflate view
     */
    fun inflateView(layoutId: Int): View {
        return layoutInflater.inflate(layoutId, null)
    }

    fun getItem():Any{
        return item
    }


}