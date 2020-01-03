package com.liang.tablayout3x.indicator

import android.animation.TypeEvaluator
import android.graphics.Rect

abstract class IndicatorTypeEvaluator : TypeEvaluator<Rect>

class DefIndicatorEvaluator : IndicatorTypeEvaluator() {

    override fun evaluate(
        fraction: Float,
        startValue: Rect,
        endValue: Rect
    ): Rect {
        val left = startValue.left + fraction * (endValue.left - startValue.left)
        val right = startValue.right + fraction * (endValue.right - startValue.right)
        val top = startValue.top + fraction * (endValue.top - startValue.top)
        val bottom = startValue.bottom + fraction * (endValue.bottom - startValue.bottom)
        return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }
}

class TransitionIndicatorEvaluator : IndicatorTypeEvaluator() {
    override fun evaluate(
        fraction: Float,
        startValue: Rect,
        endValue: Rect
    ): Rect {
        var fractionL: Float
        var fractionR: Float

        if (startValue.left < endValue.left || startValue.top < endValue.top) {
            fractionL = fraction * 2 - 1
            fractionR = fraction * 2
            if (fractionL < 0) {
                fractionL = 0f
            }
            if (1 - fractionR < 0) {
                fractionR = 1f
            }
        } else {
            fractionR = fraction * 2 - 1
            fractionL = fraction * 2
            if (fractionR < 0) {
                fractionR = 0f
            }
            if (1 - fractionL < 0) {
                fractionL = 1f
            }
        }

        val left = startValue.left + fractionL * (endValue.left - startValue.left)
        val right = startValue.right + fractionR * (endValue.right - startValue.right)
        val top = startValue.top + fractionL * (endValue.top - startValue.top)
        val bottom = startValue.bottom + fractionR * (endValue.bottom - startValue.bottom)
        return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }
}
