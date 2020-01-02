package com.liang.utils

import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v4.graphics.ColorUtils
import android.util.StateSet


object RippleUtils {
    var USE_FRAMEWORK_RIPPLE = false
    private val PRESSED_STATE_SET: IntArray
    private val HOVERED_FOCUSED_STATE_SET: IntArray
    private val FOCUSED_STATE_SET: IntArray
    private val HOVERED_STATE_SET: IntArray
    private val SELECTED_PRESSED_STATE_SET: IntArray
    private val SELECTED_HOVERED_FOCUSED_STATE_SET: IntArray
    private val SELECTED_FOCUSED_STATE_SET: IntArray
    private val SELECTED_HOVERED_STATE_SET: IntArray
    private val SELECTED_STATE_SET: IntArray

    fun convertToRippleDrawableColor(rippleColor: ColorStateList): ColorStateList {
        val size: Byte
        val states: Array<IntArray?>
        val colors: IntArray
        //        byte i;
        var i: Int
        return if (USE_FRAMEWORK_RIPPLE) {
            size = 2
            states = arrayOfNulls(size.toInt())
            colors = IntArray(size.toInt())
            i = 0
            states[i] = SELECTED_STATE_SET
            colors[i] =
                getColorForState(rippleColor, SELECTED_PRESSED_STATE_SET)
            i += 1
            states[i] = StateSet.NOTHING
            colors[i] = getColorForState(rippleColor, PRESSED_STATE_SET)
            ++i
            ColorStateList(states, colors)
        } else {
            size = 10
            states = arrayOfNulls(size.toInt())
            colors = IntArray(size.toInt())
            i = 0
            states[i] = SELECTED_PRESSED_STATE_SET
            colors[i] =
                getColorForState(rippleColor, SELECTED_PRESSED_STATE_SET)
            i += 1
            states[i] = SELECTED_HOVERED_FOCUSED_STATE_SET
            colors[i] = getColorForState(
                rippleColor,
                SELECTED_HOVERED_FOCUSED_STATE_SET
            )
            ++i
            states[i] = SELECTED_FOCUSED_STATE_SET
            colors[i] =
                getColorForState(rippleColor, SELECTED_FOCUSED_STATE_SET)
            ++i
            states[i] = SELECTED_HOVERED_STATE_SET
            colors[i] =
                getColorForState(rippleColor, SELECTED_HOVERED_STATE_SET)
            ++i
            states[i] = SELECTED_STATE_SET
            colors[i] = 0
            ++i
            states[i] = PRESSED_STATE_SET
            colors[i] = getColorForState(rippleColor, PRESSED_STATE_SET)
            ++i
            states[i] = HOVERED_FOCUSED_STATE_SET
            colors[i] =
                getColorForState(rippleColor, HOVERED_FOCUSED_STATE_SET)
            ++i
            states[i] = FOCUSED_STATE_SET
            colors[i] = getColorForState(rippleColor, FOCUSED_STATE_SET)
            ++i
            states[i] = HOVERED_STATE_SET
            colors[i] = getColorForState(rippleColor, HOVERED_STATE_SET)
            ++i
            states[i] = StateSet.NOTHING
            colors[i] = 0
            ++i
            ColorStateList(states, colors)
        }
    }

    @ColorInt
    private fun getColorForState(rippleColor: ColorStateList?, state: IntArray): Int {
        val color: Int = rippleColor?.getColorForState(state, rippleColor.defaultColor) ?: 0
        return if (USE_FRAMEWORK_RIPPLE) doubleAlpha(color) else color
    }

    @ColorInt
    @TargetApi(21)
    private fun doubleAlpha(@ColorInt color: Int): Int {
        val alpha = (2 * Color.alpha(color)).coerceAtMost(255)
        return ColorUtils.setAlphaComponent(color, alpha)
    }

    init {
        USE_FRAMEWORK_RIPPLE = Build.VERSION.SDK_INT >= 21
        PRESSED_STATE_SET = intArrayOf(16842919)
        HOVERED_FOCUSED_STATE_SET = intArrayOf(16843623, 16842908)
        FOCUSED_STATE_SET = intArrayOf(16842908)
        HOVERED_STATE_SET = intArrayOf(16843623)
        SELECTED_PRESSED_STATE_SET = intArrayOf(16842913, 16842919)
        SELECTED_HOVERED_FOCUSED_STATE_SET = intArrayOf(16842913, 16843623, 16842908)
        SELECTED_FOCUSED_STATE_SET = intArrayOf(16842913, 16842908)
        SELECTED_HOVERED_STATE_SET = intArrayOf(16842913, 16843623)
        SELECTED_STATE_SET = intArrayOf(16842913)
    }
}