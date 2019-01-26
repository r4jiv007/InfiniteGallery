package com.mantis.customgallery

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout

open class CustomGallery<T> : FrameLayout {
	
	// member definition
	
	var childLayoutId: Int = 0
	
	val itemList: MutableList<T> = mutableListOf()
	
	
	private var mTouchSlop: Int = 0
	private lateinit var layoutInflater: LayoutInflater
	private lateinit var layoutParams: LayoutParams
	
	// constructor definition
	
	
	constructor(context: Context) : super(context) {
		init()
	}
	
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
		init()
	}
	
	private fun init() {
		mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
		layoutInflater = LayoutInflater.from(context)
		layoutParams = LayoutParams(
			(Utils.convertDpToPixel(300f, context)),
			Utils.convertDpToPixel(300f, context)
		                           )
		addChild()
		addChild()
		addChild()
		addChild()
	}
	
	private fun addChild() {
		val child: FrameLayout = layoutInflater.inflate(R.layout.item_view, null) as FrameLayout
		
		addTouchListenerToView(child)
		setScale(child)
		addView(child, 0, getChildLayoutParams())
		
	}
	
	private fun setScale(view: View) {
		view.scaleX = 1 - (.05f * childCount)
		view.scaleY = 1 - (.05f * childCount)
	}
	
	private fun getChildLayoutParams(): LayoutParams {
		
		var layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
		layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
		layoutParams.topMargin = childCount * Utils.convertDpToPixel(20f, context)
		return layoutParams
	}
	
	var viewY: Float = 0f
	
	private fun addTouchListenerToView(view: View) {
		
		val displayMetrics = view.context.resources.displayMetrics
		view.setOnTouchListener(object : View.OnTouchListener {
			private var dx: Float = 0.toFloat()
			
			private var viewX: Float = 0f
			override fun onTouch(v: View, event: MotionEvent): Boolean {
				when (event.action and MotionEvent.ACTION_MASK) {
					MotionEvent.ACTION_DOWN -> {
						dx = view.x - event.rawX
						viewX = view.x
					}
					MotionEvent.ACTION_POINTER_UP -> {
					}
					MotionEvent.ACTION_UP -> {
						if (view.x < displayMetrics.widthPixels / 2) {
							moveView(view, viewX)
						} else {
							moveView(view, displayMetrics.widthPixels.toFloat())
						}
						
					}
					MotionEvent.ACTION_MOVE -> {
						moveView(view, event.rawX + dx)
					}
				}
				return true
			}
		})
		
	}
	
	private fun resetView(view: View, posX: Float) {
	
	}
	
	private fun moveView(view: View, posX: Float) {
		view.animate()
			.x(posX)
			.setDuration(0)
			.start()
	}
	
	var dx1: Float = 0f
	var dy1: Float = 0f
	
	var counter: Int = 0
	private fun moveView(view: View, dx: Float, dy: Float) {
		counter++
		if ((dx1 == dx && dy1 == dy) || ((Math.abs(Math.abs(dx1) - Math.abs(dx))) < 20 && (Math.abs(
				Math.abs(dy1) - Math.abs(dy)
		                                                                                           )) < 20)
		) {
			return
		}
		
		dx1 = dx
		dy1 = dy
		
		var params: LayoutParams = view.layoutParams as LayoutParams
		params.leftMargin += dx.toInt()
		
		params.topMargin += dy.toInt()
		Log.i(
			"*Margin",
			"dx " + dx + ", dy " + dy + ", leftMargin " + params.leftMargin.toString() + ", topMargin " + params.leftMargin.toString()
		     )
		view.layoutParams = params
		Log.i("counter", "" + counter)
		
	}
	
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		return super.onTouchEvent(event)
	}
	
	
}
