package com.liang.jtab.indicator;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.view.Gravity;

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
    private int color = Color.GRAY;

    private Paint indicatorPaint;
    private Path trianglePath;
    private int gravity = Gravity.BOTTOM;

    private GradientDrawable indicatorDrawable;


    public GradientDrawable getBackground() {
        return indicatorDrawable;
    }

    public JIndicator() {
        initPaint();
        initGradientDrawable();
    }

    /**
     * 设置Indicator的形状
     *
     * @param type
     */
    public JIndicator setType(int type) {
        this.type = type;
        update();
        return this;
    }

    /**
     * 设置Indicator的位置（上、中、下）
     *
     * @param gravity Gravity.TOP、Gravity.CENTER、Gravity.BOTTOM
     */
    public JIndicator setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * 设置Indicator的圆角角度
     * 对TYPE_RECT有效
     * @param radius
     */
    public JIndicator setRadius(int radius) {
        this.radius = radius;
        update();
        return this;
    }

    /**
     * 设置Indicator的颜色
     *
     * @param color
     */
    public JIndicator setColor(@ColorInt int color) {
        this.color = color;
        update();
        return this;
    }

    private void update() {
        if (type == TYPE_TRIANGLE) {
            if (indicatorPaint == null) {
                indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                indicatorPaint.setAntiAlias(true);
            }
            indicatorPaint.setColor(color);
        } else {
            if (indicatorDrawable == null) {
                indicatorDrawable = new GradientDrawable();
            }
            indicatorDrawable.setColor(color);

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
    }

    private void initPaint() {
        if (indicatorPaint == null) {
            indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            indicatorPaint.setAntiAlias(true);
            indicatorPaint.setColor(color);
        }
    }

    private void initGradientDrawable() {
        if (indicatorDrawable == null) {
            indicatorDrawable = new GradientDrawable();
            indicatorDrawable.setColor(color);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void draw(Canvas canvas, int tabHeight) {

        if (type == TYPE_TRIANGLE) {
            if (trianglePath == null) {
                trianglePath = new Path();
            }
            float triangleWidth = right - left;
            trianglePath.reset();
            trianglePath.moveTo(0, 0);
            trianglePath.lineTo(triangleWidth, 0);
            if (gravity == Gravity.TOP) {
                trianglePath.lineTo(triangleWidth / 2, getHeight());
                trianglePath.close();
                canvas.translate(left, 0);
            } else {
                trianglePath.lineTo(triangleWidth / 2, -getHeight());
                trianglePath.close();
                canvas.translate(left, tabHeight);
            }
            canvas.drawPath(trianglePath, indicatorPaint);
        } else {
            if (gravity == Gravity.TOP) {
                indicatorDrawable.setBounds((int) left, 0, (int) right, getHeight());
            } else if (gravity == Gravity.BOTTOM) {
                indicatorDrawable.setBounds((int) left, tabHeight - getHeight(), (int) right, tabHeight);
            } else {
                indicatorDrawable.setBounds((int) left, (tabHeight - getHeight()) / 2, (int) right, (tabHeight - getHeight()) / 2 + getHeight());
            }
            indicatorDrawable.draw(canvas);
        }
    }
}
