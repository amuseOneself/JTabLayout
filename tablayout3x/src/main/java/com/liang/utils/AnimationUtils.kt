package com.liang.utils

import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlin.math.roundToInt

object AnimationUtils {
    val linearInterpolator = LinearInterpolator()
    val fastOutSlowInInterpolator = FastOutSlowInInterpolator()
    val fastOutLinearInInterpolator = FastOutLinearInInterpolator()
    val linearOutSlowInInterpolator = LinearOutSlowInInterpolator()
    val decelerateInterpolator = DecelerateInterpolator()
    val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()

    fun lerp(
        startValue: Float,
        endValue: Float,
        fraction: Float
    ): Float {
        return startValue + fraction * (endValue - startValue)
    }

    fun lerp(startValue: Int, endValue: Int, fraction: Float): Int {
        return startValue + (fraction * (endValue - startValue).toFloat()).roundToInt()
    }
}

