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
						var diff = Math.abs(viewX-view.x)
						if (diff < displayMetrics.widthPixels / 3 && (view.x + view.width) > displayMetrics.widthPixels / 3) {
							resetView(view, viewX)
						} else {
							if (view.x > displayMetrics.widthPixels / 3) {
								moveView(view, displayMetrics.widthPixels.toFloat())
							} else if ((view.x + view.width) < displayMetrics.widthPixels / 3){
								moveView(view, -1*displayMetrics.widthPixels.toFloat())
							}
						}
						
					}
					MotionEvent.ACTION_MOVE -> {
						moveViewImmediate(view, event.rawX + dx)
					}
				}
				return true
			}
		})
		
	}
	
	private fun resetView(view: View, posX: Float) {
		view.animate()
			.x(posX)
			.setDuration(50)
			.start()
	}
	
	private fun moveViewImmediate(view: View, posX: Float) {
		view.animate()
			.x(posX)
			.setDuration(0)
			.start()
	}
	
	private fun moveView(view: View, posX: Float) {
		view.animate()
			.x(posX)
			.setDuration(200)
			.start()
	}
	
	var dx1: Float = 0f
	var dy1: Float = 0f
	
	var counter: Int = 0
	
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		return super.onTouchEvent(event)
	}
	
	
}
