package com.liang.tablayout3x.indicator

import android.animation.TypeEvaluator

abstract class IndicatorTypeEvaluator : TypeEvaluator<IndicatorPoint>

class DefIndicatorEvaluator : IndicatorTypeEvaluator() {

    override fun evaluate(
        fraction: Float,
        startValue: IndicatorPoint,
        endValue: IndicatorPoint
    ): IndicatorPoint {
        val left = startValue.left + fraction * (endValue.left - startValue.left)
        val right = startValue.right + fraction * (endValue.right - startValue.right)
        val indicatorPoint = IndicatorPoint()
        indicatorPoint.left = left
        indicatorPoint.right = right
        return indicatorPoint
    }
}


class TransitionIndicatorEvaluator : IndicatorTypeEvaluator() {
    override fun evaluate(
        fraction: Float,
        startValue: IndicatorPoint,
        endValue: IndicatorPoint
    ): IndicatorPoint {
        var fractionL: Float
        var fractionR: Float
        if (startValue.left < endValue.left) {
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
        val indicatorPoint = IndicatorPoint()
        indicatorPoint.left = left
        indicatorPoint.right = right
        return indicatorPoint
    }
}
