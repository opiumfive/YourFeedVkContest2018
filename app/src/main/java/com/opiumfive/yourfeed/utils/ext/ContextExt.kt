package com.opiumfive.yourfeed.utils.ext

import android.content.Context

val Context.screenWidthPx
    get() = resources.displayMetrics.widthPixels

fun Context.dp2px(value: Int): Int = (value * this.resources.displayMetrics.density).toInt()