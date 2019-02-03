package com.mantis.infinitegallery

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout


open class InfiniteGallery<S, T : BaseInfiniteView<S>> : FrameLayout {

    // member definition


    private var mTouchSlop: Int = 0
    private lateinit var layoutParams: LayoutParams
    lateinit var annimHanlder: Handler

    var pauseAnnim: Boolean = false

    val DEFAULT_CHILD_NUM_TO_SHOW: Int = 3

    var numChildToShow: Int = DEFAULT_CHILD_NUM_TO_SHOW

    var childViewList = mutableListOf<T>()
    var itemList = mutableListOf<S>()

    val DIRECTION_RIGHT = 1
    val DIRECTION_LEFT = -1
    var direction: Int = DIRECTION_RIGHT

    // constructor definition


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    lateinit var displayMetrics: DisplayMetrics

    private fun init() {
        displayMetrics = context.resources.displayMetrics
        annimHanlder = Handler()
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        layoutParams = LayoutParams(
            (Utils.convertDpToPixel(300f, context)),
            Utils.convertDpToPixel(300f, context)
        )

    }

    fun addChild(view: T) {
        childViewList.add(view)
    }

//    private fun getChild(context: Context) :T{
//
//    }

    inline fun <reified T> getChild(context: Context): T {
       return  T::class.java.newInstance()
    }

    fun addChilds(views: MutableList<T>): InfiniteGallery<S, T> {
        childViewList = views
        return this
    }
    fun postItemList(dataList: MutableList<S>) {
        this.itemList = dataList
    }

    fun display() {
        post {
            removeAllViews()
            for (item in itemList) {

                attachChild((getChild(context)as BaseInfiniteView).bindView(item))
            }
            startAnimation()
        }
    }

    private fun attachChild(child: View) {
        if (childCount == 0) {
            addTouchListenerToView(child)
        }
        setScale(child, childCount)
        addView(child, 0, getChildLayoutParams())
        if (childCount > numChildToShow) {
            child.alpha = 0.0f
        }
    }

    private fun reAttachChild(child: View, posX: Float) {
        setScale(child, numChildToShow - 1)
        addView(child, 0, getChildLayoutParams())
        child.animate()
            .x(posX).duration = 200

        if (childViewList.size > numChildToShow) {
            child.animate().alpha(0.0f)
        }
        child.animate().start()
    }

    private fun stopAnimaiton() {
        annimHanlder.removeCallbacksAndMessages(null)
        return

    }

    private fun startAnimation() {
        annimHanlder.postDelayed(object : Runnable {
            override fun run() {
                moveViewInDirection(direction)
                annimHanlder.postDelayed(this, 2000)
            }

        }, 2000)
    }


    private fun moveViewInDirection(dir: Int) {
        var view = getChildAt(childCount - 1)
        if (dir == DIRECTION_RIGHT) {
            moveViewToRight(view, view.x)
        } else if (dir == DIRECTION_LEFT) {
            moveViewToLeft(view, view.x)
        }
    }


    private fun setScale(view: View, pos: Int) {
        val scaleVal = 1 - (.05f * pos)
        view.scaleX = scaleVal
        view.scaleY = scaleVal
    }


    fun getChildLayoutParams(): LayoutParams {
        val layoutParams = LayoutParams(MATCH_PARENT, height - (numChildToShow * Utils.convertDpToPixel(20f, context)))
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        val pos = if (childCount >= numChildToShow) numChildToShow - 1 else childCount
        layoutParams.topMargin = pos * Utils.convertDpToPixel(20f, context)
        layoutParams.leftMargin = Utils.convertDpToPixel(10f, context)
        layoutParams.rightMargin = Utils.convertDpToPixel(10f, context)
        return layoutParams
    }

    private fun updateChildLayoutParams(view: View, pos: Int) {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        val finalPos = if (pos >= numChildToShow) numChildToShow - 1 else pos
        val animator = ValueAnimator.ofInt(layoutParams.topMargin, finalPos * Utils.convertDpToPixel(20f, context))
        animator.addUpdateListener { valueAnimator ->
            layoutParams.topMargin = valueAnimator.animatedValue as Int
            view.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }

    private fun updateChildScale(view: View, pos: Int) {
        val finalPos = if (pos >= numChildToShow) numChildToShow - 1 else pos
        val scaleVal = 1 - (.05f * finalPos)
        view.animate()
            .scaleX(scaleVal)
            .scaleY(scaleVal)
            .setDuration(200)
            .start()
    }


    private fun addTouchListenerToView(view: View) {
        view.setOnTouchListener(object : View.OnTouchListener {
            private var dx: Float = 0.toFloat()

            private var viewX: Float = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (disableTouch) {
                    return true
                }
                stopAnimaiton()
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        dx = view.x - event.rawX
                        viewX = view.x
                    }
                    MotionEvent.ACTION_POINTER_UP -> {
                    }
                    MotionEvent.ACTION_UP -> {
                        direction = if (viewX - view.x < 0) 1 else -1
                        val diff = Math.abs(viewX - view.x)
                        if (diff < view.width / 6) {
                            resetView(view, viewX)
                        } else {
                            if (direction == DIRECTION_RIGHT) {
                                moveViewToRight(view, viewX)
                            } else if (direction == DIRECTION_LEFT) {
                                moveViewToLeft(view, viewX)
                            }
                        }
                        pauseAnnim = false
                        startAnimation()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        moveViewImmediate(view, event.rawX + dx)
                    }
                }
                return true
            }
        })

    }

    private fun moveViewToRight(view: View, initialPos: Float) {
        moveView(view, displayMetrics.widthPixels.toFloat(), initialPos)
    }

    private fun moveViewToLeft(view: View, initialPos: Float) {
        moveView(view, -1 * displayMetrics.widthPixels.toFloat(), initialPos)
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

    var disableTouch: Boolean = false

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
                    disableTouch = false
                }

                override fun onAnimationCancel(animation: Animator?) {
                    disableTouch = false
                }

                override fun onAnimationStart(animation: Animator?) {
                    disableTouch = true
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

    private fun makeViewTransparent(view: View) {
        view.alpha = 0.0f
    }

    private fun makeViewOpaque(view: View) {
        view.alpha = 1.0f
    }

    private fun reassignTouchListenr(view: View) {
        view.setOnTouchListener(null)
        addTouchListenerToView(this.getChildAt(childCount - 1))
    }

    private fun scaleAllViews() {
        val count = childCount
        for (i in 0 until count) {
            updateChildScale(getChildAt(i), count - 1 - i)
            if ((i >= count - numChildToShow)) {
                makeViewOpaque(getChildAt(i))
            }
        }
    }

    private fun handleMarginForViews() {
        val count = childCount
        for (i in 0 until count) {
            updateChildLayoutParams(getChildAt(i), count - 1 - i)
        }
    }

}
