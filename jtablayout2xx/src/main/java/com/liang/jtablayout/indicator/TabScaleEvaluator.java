package com.liang.jtablayout.indicator;


import android.animation.TypeEvaluator;
import android.util.Log;

public class TabScaleEvaluator implements TypeEvaluator<Float> {
    @Override
    public Float evaluate(float fraction, Float startValue, Float endValue) {
        float s = startValue + fraction * (endValue - startValue);
        Log.e("evaluate", " s: " + s);
        return s;
    }
//    @Override
//    public IndicatorPoint evaluate(float fraction, IndicatorPoint startValue, IndicatorPoint endValue) {
//        float left = startValue.left + fraction * (endValue.left - startValue.left);
//        float right = startValue.right + fraction * (endValue.right - startValue.right);
//        IndicatorPoint indicatorPoint = new IndicatorPoint();
//        indicatorPoint.left = left;
////        Log.e("evaluate", " left: " + left);
//        indicatorPoint.right = right;
//        return indicatorPoint;
//    }
}
