package com.opiumfive.yourfeed.utils.recyclerHelpers

import android.support.v7.widget.RecyclerView

interface OnSlideListener {
    fun onSliding(viewHolder: RecyclerView.ViewHolder, ratio: Float, direction: Int)
    fun onSlided(viewHolder: RecyclerView.ViewHolder, direction: Int)
}