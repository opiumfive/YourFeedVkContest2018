package com.opiumfive.yourfeed.utils.views

import android.content.Context
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.widget.FrameLayout

class SquareFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val snHeight = MeasureSpec.getSize(widthMeasureSpec)
        val snHeightSpec = MeasureSpec.makeMeasureSpec(snHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, snHeightSpec)
    }
}