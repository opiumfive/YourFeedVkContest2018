package com.opiumfive.yourfeed.utils.recyclerHelpers

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class ItemTouchHelperCallback(
    private val adapter: RecyclerView.Adapter<*>?,
    private val listener: OnSlideListener?
) : ItemTouchHelper.Callback() {

    var enabled = true

    override fun getMovementFlags(rv: RecyclerView, vh: RecyclerView.ViewHolder): Int {
        val dragFlags = 0
        var slideFlags = 0
        val layoutManager = rv.layoutManager
        if (layoutManager is VkNewsLayoutManager && enabled) {
            slideFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        }
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, slideFlags)
    }

    override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        viewHolder.itemView.setOnTouchListener(null)

        adapter?.notifyDataSetChanged()

        listener?.onSlided(
            viewHolder,
            if (direction == ItemTouchHelper.LEFT) SLIDED_LEFT else SLIDED_RIGHT
        )
    }

    override fun isItemViewSwipeEnabled() = false

    override fun onChildDraw(
        c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive)
        val itemView = vh.itemView
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            var ratio = dX / getThreshold(rv, vh)

            if (ratio > 1) {
                ratio = 1f
            } else if (ratio < -1) {
                ratio = -1f
            }

            //rotate and scale depending on direction
            itemView.rotation = ratio * DEFAULT_ROTATE_DEGREE
            when {
                ratio > 0 -> {
                    itemView.scaleX = 1 + Math.abs(ratio) * DEFAULT_SCALE_SLIDE
                    itemView.scaleY = 1 + Math.abs(ratio) * DEFAULT_SCALE_SLIDE
                }
                ratio < 0 -> {
                    itemView.scaleX = 1 - Math.abs(ratio) * DEFAULT_SCALE_SLIDE
                    itemView.scaleY = 1 - Math.abs(ratio) * DEFAULT_SCALE_SLIDE
                }
                else -> {
                    itemView.scaleX = 1f
                    itemView.scaleY = 1f
                }
            }

            val childCount = rv.childCount
            if (childCount > DEFAULT_SHOW_ITEM) {
                for (position in 0 until childCount - 1) {
                    val index = childCount - position - 1
                    val view = rv.getChildAt(position)
                    view.scaleX = 1 - index * DEFAULT_SCALE + Math.abs(ratio) * DEFAULT_SCALE
                    view.scaleY = 1 - index * DEFAULT_SCALE + Math.abs(ratio) * DEFAULT_SCALE
                }
            }

            if (ratio != 0f) {
                listener?.onSliding(
                    vh, ratio, if (ratio < 0) SLIDING_LEFT else SLIDING_RIGHT
                )
            } else {
                listener?.onSliding(vh, ratio, SLIDING_NONE)
            }
        }
    }

    override fun clearView(rv: RecyclerView, vh: RecyclerView.ViewHolder) {
        super.clearView(rv, vh)
        vh.itemView.rotation = 0f
    }

    private fun getThreshold(rv: RecyclerView, vh: RecyclerView.ViewHolder) = rv.width * getSwipeThreshold(vh)
}