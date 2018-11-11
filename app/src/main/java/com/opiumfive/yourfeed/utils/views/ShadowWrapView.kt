package com.opiumfive.yourfeed.utils.views

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.AttrRes
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.opiumfive.yourfeed.R


class ShadowWrapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var shadowColor = 0
    private var shadowRadius = 0f
    private var cornerRadius = 0f
    private var shadowRadius2 = 0f
    private var isOnlyTopCorners = false
    private var isWithInnerPadding = true
    private var dx = 0f
    private var dy = 0f
    private var dx2 = 0f
    private var dy2 = 0f

    private var invalidateShadowOnSizeChanged = true
    private var forceInvalidateShadow = false

    init {
        initAttributes(context, attrs)

        if (isWithInnerPadding) {

            val xPadding = Math.max(shadowRadius2 + Math.abs(dx2), shadowRadius + Math.abs(dx)).toInt()
            val yPadding = Math.max(shadowRadius2 + Math.abs(dy2), shadowRadius + Math.abs(dy)).toInt()
            setPadding(xPadding, yPadding, xPadding, yPadding)
        }
    }

    override fun getSuggestedMinimumWidth() = 0

    override fun getSuggestedMinimumHeight() = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && (background == null || invalidateShadowOnSizeChanged || forceInvalidateShadow)) {
            forceInvalidateShadow = false
            setBackgroundCompat(w, h)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (forceInvalidateShadow) {
            forceInvalidateShadow = false
            setBackgroundCompat(right - left, bottom - top)
        }
    }

    fun setInvalidateShadowOnSizeChanged(invalidateShadowOnSizeChanged: Boolean) {
        this.invalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged
    }

    fun invalidateShadow() {
        forceInvalidateShadow = true
        requestLayout()
        invalidate()
    }

    private fun setBackgroundCompat(w: Int, h: Int) {
        val bitmap = createShadowBitmap(w, h, cornerRadius, shadowRadius, dx, dy, shadowColor, Color.TRANSPARENT)
        val drawable = BitmapDrawable(resources, bitmap)

        background = drawable
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val attr = getTypedArray(context, attrs, R.styleable.ShadowWrapView) ?: return

        try {
            cornerRadius = attr.getDimension(R.styleable.ShadowWrapView_cornerRadius, 0f)
            shadowRadius = attr.getDimension(R.styleable.ShadowWrapView_shadowRadius, 0f)
            dx = attr.getDimension(R.styleable.ShadowWrapView_dx, 0f)
            dy = attr.getDimension(R.styleable.ShadowWrapView_dy, 0f)
            shadowRadius2 = attr.getDimension(R.styleable.ShadowWrapView_shadowRadius2, 0f)
            dx2 = attr.getDimension(R.styleable.ShadowWrapView_dx2, 0f)
            dy2 = attr.getDimension(R.styleable.ShadowWrapView_dy2, 0f)
            shadowColor = attr.getColor(
                R.styleable.ShadowWrapView_shadowColor,
                resources.getColor(R.color.default_shadow_color)
            )
            isOnlyTopCorners = attr.getBoolean(R.styleable.ShadowWrapView_radiusOnlyTop, false)
            isWithInnerPadding = attr.getBoolean(R.styleable.ShadowWrapView_addInnerPadding, true)
        } finally {
            attr.recycle()
        }
    }

    private fun getTypedArray(context: Context, attributeSet: AttributeSet?, attr: IntArray): TypedArray? {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0)
    }

    private fun createShadowBitmap(
        shadowWidth: Int, shadowHeight: Int, cornerRadius: Float, shadowRadius: Float,
        dx: Float, dy: Float, shadowColor: Int, fillColor: Int
    ): Bitmap {

        val output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(output)

        val shadowRect = if (isWithInnerPadding) {
            RectF(shadowRadius, shadowRadius, shadowWidth - shadowRadius, shadowHeight - shadowRadius)
        } else {
            RectF(0f, shadowRadius, shadowWidth.toFloat(), shadowHeight.toFloat())
        }

        val shadowRect2 = if (isWithInnerPadding) {
            RectF(shadowRadius2, shadowRadius, shadowWidth - shadowRadius2, shadowHeight - shadowRadius2)
        } else {
            RectF(0f, shadowRadius2, shadowWidth.toFloat(), shadowHeight.toFloat())
        }

        if (dy > 0) {
            shadowRect.top += dy
            shadowRect.bottom -= dy
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy)
            shadowRect.bottom -= Math.abs(dy)
        }

        if (dx > 0) {
            shadowRect.left += dx
            shadowRect.right -= dx
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx)
            shadowRect.right -= Math.abs(dx)
        }

        if (dy2 > 0) {
            shadowRect2.top += dy2
            shadowRect2.bottom -= dy2
        } else if (dy2 < 0) {
            shadowRect2.top += Math.abs(dy2)
            shadowRect2.bottom -= Math.abs(dy2)
        }

        if (dx2 > 0) {
            shadowRect2.left += dx2
            shadowRect2.right -= dx2
        } else if (dx2 < 0) {
            shadowRect2.left += Math.abs(dx2)
            shadowRect2.right -= Math.abs(dx2)
        }

        val shadowPaint = Paint()
        shadowPaint.isAntiAlias = true
        shadowPaint.color = fillColor
        shadowPaint.style = Paint.Style.FILL

        if (!isInEditMode) {
            shadowPaint.setShadowLayer(shadowRadius, dx, dy2, shadowColor)
        }

        if (isOnlyTopCorners) {
            canvas.drawRect(
                shadowRect.left,
                shadowRect.top + cornerRadius,
                shadowRect.right,
                shadowRect.bottom,
                shadowPaint
            )
        }

        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint)
        if (shadowRadius2 > 0) {
            canvas.drawRoundRect(shadowRect2, cornerRadius, cornerRadius, shadowPaint)
        }

        return output
    }
}