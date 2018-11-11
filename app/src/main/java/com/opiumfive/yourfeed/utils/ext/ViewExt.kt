package com.opiumfive.yourfeed.utils.ext

import android.view.View
import android.view.animation.AnimationUtils

fun View.visible(visibility: Int) = if (this.visibility != visibility) this.visibility = visibility else Unit

fun View.launchAnim(animRes: Int) = this.startAnimation(AnimationUtils.loadAnimation(this.context, animRes))

fun View.hideSmooth() = this.animate().alpha(0.0f).setDuration(300).start()

fun View.postIfNoWidth(action: () -> Unit = {}) {
    when {
        this.width > 0 -> action()
        else -> this.post { action() }
    }
}