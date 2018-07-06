package com.liang.jtab;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;

@SuppressLint("WrongConstant")
public class JIndicator extends Indicator {

    public static final int TYPE_LINE = 0;
    public static final int TYPE_RECT = 1;
    public static final int TYPE_TRIANGLE = 2;
    public static final int TYPE_RING = 3;
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

    public JIndicator setType(int type) {
        this.type = type;
        update();
        return this;
    }

    public JIndicator setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public JIndicator setRadius(int radius) {
        this.radius = radius;
        update();
        return this;
    }

    public JIndicator setColor(int color) {
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
    public void draw(Canvas canvas, float left, float right, int tabHeight) {
//        if (type == TYPE_LINE) {
////            indicatorPaint.setStrokeWidth(getHeight());
////            if (gravity == Gravity.TOP) {
////                canvas.drawLine(left, 0, right, 0, indicatorPaint);
////            } else {
////                canvas.drawLine(left, tabHeight, right, tabHeight, indicatorPaint);
////            }
////        }
////
////        if (type == TYPE_RECT) {
////            if (gravity == Gravity.TOP) {
////                indicatorDrawable.setBounds((int) left, 0, (int) right, getHeight());
////            } else if (gravity == Gravity.BOTTOM) {
////                indicatorDrawable.setBounds((int) left, tabHeight - getHeight(), (int) right, tabHeight);
////            } else {
////                indicatorDrawable.setBounds((int) left, (tabHeight - getHeight()) / 2, (int) right, (tabHeight - getHeight()) / 2 + getHeight());
////            }
////            indicatorDrawable.draw(canvas);
//////            canvas.drawRoundRect(left, (tabHeight - height) / 2, right, (tabHeight - height) / 2 + height, radius, radius, indicatorPaint);
////        }

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
