package com.liang.jtablayout.indicator;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

@SuppressLint("WrongConstant")
public class JIndicator extends Indicator {
    /**
     * Type is a line
     */
    public static final int TYPE_LINE = 0;

    /**
     * Type is a rect
     */
    public static final int TYPE_RECT = 1;

    /**
     * Type is a triangle
     */
    public static final int TYPE_TRIANGLE = 2;

    /**
     * Type is a ring.
     */
    public static final int TYPE_RING = 3;

    /**
     * Type is an ellipse
     */
    public static final int TYPE_OVAL = 4;

    private int type = TYPE_LINE;

    private int radius = 0;

    /**
     * Set the shape of Indicator
     *
     * @param type
     */
    public JIndicator setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public Drawable getIndicator() {
        return createGradientDrawable();
    }

    /**
     * Setting the corner angle of Indicator
     * Effective for TYPE_RECT
     *
     * @param radius
     */
    public JIndicator setRadius(int radius) {
        this.radius = radius;
        return this;
    }


    private Drawable createGradientDrawable() {
        GradientDrawable indicatorDrawable = new GradientDrawable();
        indicatorDrawable.setColor(getColor());

        if (type == TYPE_TRIANGLE) {

        } else {
            indicatorDrawable.setColor(getColor());
            if (type == TYPE_LINE || type == TYPE_RECT) {
                indicatorDrawable.setShape(GradientDrawable.RECTANGLE);
                indicatorDrawable.setCornerRadius(radius);
            }
            if (type == TYPE_OVAL) {
                indicatorDrawable.setShape(GradientDrawable.OVAL);
            }
            if (type == TYPE_RING) {
                indicatorDrawable.setShape(GradientDrawable.RING);
            }
        }

        return indicatorDrawable;
    }

}
