package com.liang.jtablayout.indicator;

public class TransitionIndicatorEvaluator extends IndicatorTypeEvaluator<IndicatorPoint> {
    @Override
    public IndicatorPoint evaluate(float fraction, IndicatorPoint startValue, IndicatorPoint endValue) {
        float fractionL;
        float fractionR;

        if (startValue.left < endValue.left) {
            fractionL = fraction * 2 - 1;
            fractionR = fraction * 2;
            if (fractionL < 0) {
                fractionL = 0;
            }
            if (1 - fractionR < 0) {
                fractionR = 1;
            }
        } else {
            fractionR = fraction * 2 - 1;
            fractionL = fraction * 2;
            if (fractionR < 0) {
                fractionR = 0;
            }
            if (1 - fractionL < 0) {
                fractionL = 1;
            }
        }

        float left = startValue.left + fractionL * (endValue.left - startValue.left);
        float right = startValue.right + fractionR * (endValue.right - startValue.right);
        IndicatorPoint indicatorPoint = new IndicatorPoint();
        indicatorPoint.left = left;
        indicatorPoint.right = right;
        return indicatorPoint;
    }
}
