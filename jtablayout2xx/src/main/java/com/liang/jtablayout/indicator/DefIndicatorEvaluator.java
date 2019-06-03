package com.liang.jtablayout.indicator;


import com.liang.jtablayout.indicator.IndicatorPoint;
import com.liang.jtablayout.indicator.IndicatorTypeEvaluator;

public class DefIndicatorEvaluator extends IndicatorTypeEvaluator<IndicatorPoint> {
    @Override
    public IndicatorPoint evaluate(float fraction, IndicatorPoint startValue, IndicatorPoint endValue) {
        float left = startValue.left + fraction * (endValue.left - startValue.left);
        float right = startValue.right + fraction * (endValue.right - startValue.right);
        IndicatorPoint indicatorPoint = new IndicatorPoint();
        indicatorPoint.left = left;
//        Log.e("evaluate", " left: " + left);
        indicatorPoint.right = right;
        return indicatorPoint;
    }
}
