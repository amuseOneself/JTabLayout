package com.liang.jtablayout.indicator;

public class IndicatorPoint {
    public float left = 0;
    public float right = 0;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IndicatorPoint)) {
            return false;
        }

        IndicatorPoint target = (IndicatorPoint) obj;

        return target.left == left || target.right == right;
    }
}
