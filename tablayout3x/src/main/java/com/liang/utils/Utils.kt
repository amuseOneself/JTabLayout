package com.liang.utils

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.Dimension
import android.support.annotation.StyleableRes
import android.support.v4.view.ViewCompat
import android.support.v7.content.res.AppCompatResources
import android.util.TypedValue
import android.view.View
import kotlin.math.roundToInt


/**
 * Calculate the excess color of the start color gradient to the end color gradient
 */

fun getColorFrom(startColor: Int, endColor: Int, positionOffset: Float): Int {
    val redStart = Color.red(startColor)
    val blueStart = Color.blue(startColor)
    val greenStart = Color.green(startColor)
    val redEnd = Color.red(endColor)
    val blueEnd = Color.blue(endColor)
    val greenEnd = Color.green(endColor)
    val red = (redStart + ((redEnd - redStart) * positionOffset + 0.5)).toInt()
    val greed = (greenStart + ((greenEnd - greenStart) * positionOffset + 0.5)).toInt()
    val blue = (blueStart + ((blueEnd - blueStart) * positionOffset + 0.5)).toInt()
    return Color.argb(255, red, greed, blue)
}

fun createColorStateList(defaultColor: Int, selectedColor: Int): ColorStateList {
    val states = arrayOfNulls<IntArray>(2)
    val colors = IntArray(2)
    var i = 0
    states[i] = intArrayOf(R.attr.state_selected)
    colors[i] = selectedColor
    i++
    states[i] = intArrayOf()
    colors[i] = defaultColor
    return ColorStateList(states, colors)
}


/**
 * Change from dip unit to PX (pixel) according to the resolution of mobile phone
 */
@Dimension(unit = 1)
fun Context.dpToPx(@Dimension(unit = 0) dps: Float): Int {
    return (this.resources.displayMetrics.density * dps).roundToInt()
}
/**
 * Convert the SP value to PX value to keep the text size unchanged
 */
fun Context.sp2px(sp: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, sp,
    resources.displayMetrics
).toInt()


fun Context.getColorStateList(
    attributes: TypedArray, @StyleableRes index: Int
): ColorStateList? {
    if (attributes.hasValue(index)) {
        val resourceId = attributes.getResourceId(index, 0)
        if (resourceId != 0) {
            return AppCompatResources.getColorStateList(this, resourceId)
        }
    }
    return attributes.getColorStateList(index)
}


fun Context.getDrawable(
    attributes: TypedArray, @StyleableRes index: Int
): Drawable? {
    if (attributes.hasValue(index)) {
        val resourceId = attributes.getResourceId(index, 0)
        if (resourceId != 0) {
            val value = AppCompatResources.getDrawable(this, resourceId)
            if (value != null) {
                return value
            }
        }
    }
    return attributes.getDrawable(index)
}

fun TypedArray.getIndexWithValue(@StyleableRes a: Int, @StyleableRes b: Int) =
    if (hasValue(a)) a else b

fun parseTintMode(value: Int, defaultMode: PorterDuff.Mode?) = when (value) {
    3 -> PorterDuff.Mode.SRC_OVER
    4, 6, 7, 8, 10, 11, 12, 13 -> defaultMode
    5 -> PorterDuff.Mode.SRC_IN
    9 -> PorterDuff.Mode.SRC_ATOP
    14 -> PorterDuff.Mode.MULTIPLY
    15 -> PorterDuff.Mode.SCREEN
    16 -> PorterDuff.Mode.ADD
    else -> defaultMode
}

@SuppressLint("WrongConstant")
fun View.isLayoutRtl() = ViewCompat.getLayoutDirection(this) == 1

