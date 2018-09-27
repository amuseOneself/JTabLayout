package com.liang.jtab.indicator;


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
