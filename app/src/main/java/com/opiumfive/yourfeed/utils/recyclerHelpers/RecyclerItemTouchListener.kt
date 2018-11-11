package com.opiumfive.yourfeed.utils.recyclerHelpers

import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import com.opiumfive.yourfeed.utils.ext.screenWidthPx


class RecyclerItemTouchListener(val context: Context, private val sideListener: (Int) -> Unit) :
    RecyclerView.OnItemTouchListener {

    private var gestureDetector: GestureDetectorCompat? = null

    init {
        gestureDetector = GestureDetectorCompat(context, object : GestureDetector.OnGestureListener {
            override fun onShowPress(p0: MotionEvent?) = Unit

            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                if (p0 != null) {
                    if (p0.x < context.screenWidthPx / 2) {
                        sideListener.invoke(TOUCH_LEFT)
                    }

                    if (p0.x > context.screenWidthPx / 2) {
                        sideListener.invoke(TOUCH_RIGHT)
                    }
                }

                return false
            }

            override fun onDown(p0: MotionEvent?) = false

            override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float) = false

            override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float) = false

            override fun onLongPress(p0: MotionEvent?) = Unit
        })
    }

    override fun onTouchEvent(p0: RecyclerView, p1: MotionEvent) = Unit

    override fun onInterceptTouchEvent(p0: RecyclerView, p1: MotionEvent): Boolean {
        gestureDetector?.onTouchEvent(p1)
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) = Unit
}