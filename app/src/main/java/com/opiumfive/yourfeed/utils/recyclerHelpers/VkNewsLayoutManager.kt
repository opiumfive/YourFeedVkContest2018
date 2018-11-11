package com.opiumfive.yourfeed.utils.recyclerHelpers

import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class VkNewsLayoutManager(recyclerView: RecyclerView, itemTouchHelper: ItemTouchHelper) : RecyclerView.LayoutManager() {

    private val mOnTouchListener = View.OnTouchListener { v, event ->
        val childViewHolder = recyclerView.getChildViewHolder(v)
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            itemTouchHelper.startSwipe(childViewHolder)
        }
        false
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        detachAndScrapAttachedViews(recycler!!)
        val itemCount = itemCount
        if (itemCount > DEFAULT_SHOW_ITEM) {
            for (position in DEFAULT_SHOW_ITEM downTo 0) {
                val view = recycler.getViewForPosition(position)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val widthSpace = width - getDecoratedMeasuredWidth(view)
                val heightSpace = height - getDecoratedMeasuredHeight(view)
                layoutDecoratedWithMargins(
                    view, widthSpace / 2, heightSpace / 5,
                    widthSpace / 2 + getDecoratedMeasuredWidth(view),
                    heightSpace / 5 + getDecoratedMeasuredHeight(view)
                )

                when {
                    position == DEFAULT_SHOW_ITEM -> {
                        view.scaleX = 1 - (position - 1) * DEFAULT_SCALE
                        view.scaleY = 1 - (position - 1) * DEFAULT_SCALE
                    }
                    position > 0 -> {
                        view.scaleX = 1 - position * DEFAULT_SCALE
                        view.scaleY = 1 - position * DEFAULT_SCALE
                    }
                    else -> {
                        view.scaleX = 1f
                        view.scaleY = 1f
                        view.setOnTouchListener(mOnTouchListener)
                    }
                }
            }
        } else {
            for (position in itemCount - 1 downTo 0) {
                val view = recycler.getViewForPosition(position)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val widthSpace = width - getDecoratedMeasuredWidth(view)
                val heightSpace = height - getDecoratedMeasuredHeight(view)
                layoutDecoratedWithMargins(
                    view, widthSpace / 2, heightSpace / 5,
                    widthSpace / 2 + getDecoratedMeasuredWidth(view),
                    heightSpace / 5 + getDecoratedMeasuredHeight(view)
                )

                if (position > 0) {
                    view.scaleX = 1 - position * DEFAULT_SCALE
                    view.scaleY = 1 - position * DEFAULT_SCALE
                } else {
                    view.setOnTouchListener(mOnTouchListener)
                }
            }
        }
    }
}