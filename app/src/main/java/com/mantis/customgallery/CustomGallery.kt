package com.mantis.customgallery

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout


open class CustomGallery : FrameLayout {

    // member definition


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


    val DEFAULT_CHILD_NUM_TO_SHOW: Int = 3

    var numChildToShow: Int = DEFAULT_CHILD_NUM_TO_SHOW

    private fun addChild() {
        val child: FrameLayout = layoutInflater.inflate(R.layout.item_view, null) as FrameLayout
        if (childCount == 0) {
            addTouchListenerToView(child)
        }
        setScale(child, childCount)
        addView(child, 0, getChildLayoutParams())
        when (childCount) {
            1 -> child.setBackgroundColor(Color.BLUE)
            2 -> child.setBackgroundColor(Color.RED)
            3 -> child.setBackgroundColor(Color.GREEN)
            4 -> child.setBackgroundColor(Color.CYAN)
        }
    }

    private fun reAttachChild(child: View, posX: Float) {
        setScale(child, numChildToShow - 1)
        addView(child, 0, getChildLayoutParams())
        child.animate()
            .x(posX)
            .setDuration(200)
            .start()

    }

    private fun setScale(view: View, pos: Int) {

        val scaleVal = 1 - (.05f * pos)
        view.scaleX = scaleVal
        view.scaleY = scaleVal
    }

    private fun setScale(view: View, pos: Int, animate: Boolean) {
        if (!animate) {
            setScale(view, pos)
            return
        }

        val scaleVal = 1 - (.05f * pos)
        view.animate()
            .scaleX(scaleVal)
            .scaleY(scaleVal)
            .setDuration(200)
            .start()
    }

    val DIRECTION_RIGHT = 1
    val DIRECTION_LEFT = -1
    var direction: Int = DIRECTION_RIGHT

    fun getChildLayoutParams(): LayoutParams {
        val layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        val pos = if (childCount >= numChildToShow) numChildToShow - 1 else childCount
        layoutParams.topMargin = pos * Utils.convertDpToPixel(20f, context)
        return layoutParams
    }

    private fun updateChildLayoutParams(view: View, pos: Int) {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        val animator = ValueAnimator.ofInt(layoutParams.topMargin, pos * Utils.convertDpToPixel(20f, context))
        animator.addUpdateListener { valueAnimator ->
            layoutParams.topMargin = valueAnimator.animatedValue as Int
            view.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }


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
                        val sign = viewX - view.x
                        val diff = Math.abs(viewX - view.x)
                        if (diff < view.width / 3) {
                            resetView(view, viewX)
                        } else {
                            if (sign < 0) {
                                moveView(view, displayMetrics.widthPixels.toFloat(), viewX)
                            } else {
                                moveView(view, -1 * displayMetrics.widthPixels.toFloat(), viewX)
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
        Log.i("posX", "" + posX)
        view.animate()
            .x(posX)
            .setDuration(0)
            .start()
    }

    private fun moveView(view: View, posX: Float, initialPos: Float) {
        view.animate()
            .x(posX)
            .setDuration(200)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    moveViewToBack(view, initialPos)
                    view.animate().setListener(null)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })
            .start()
    }

    private fun moveViewToBack(view: View, posX: Float) {
        removeView(view)
        reassignTouchListenr(view)
        handleMarginForViews()
        scaleAllViews()
        reAttachChild(view, posX)

    }

    private fun reassignTouchListenr(view: View) {
        view.setOnTouchListener(null)
        addTouchListenerToView(this.getChildAt(childCount - 1))
    }

    private fun scaleAllViews() {
        val count = childCount
        for (i in 0 until count) {
            setScale(getChildAt(i), count - 1 - i, true)
        }
    }

    private fun handleMarginForViews() {
        val count = childCount
        for (i in 0 until count) {
            updateChildLayoutParams(getChildAt(i), count - 1 - i)
        }
    }

}
