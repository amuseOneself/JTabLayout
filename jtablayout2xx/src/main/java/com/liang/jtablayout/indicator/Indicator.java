package com.liang.jtablayout.indicator;


import android.graphics.drawable.Drawable;

public abstract class Indicator {

    public static final int INDICATOR_GRAVITY_BOTTOM = 0;
    public static final int INDICATOR_GRAVITY_CENTER = 1;
    public static final int INDICATOR_GRAVITY_TOP = 2;
    public static final int INDICATOR_GRAVITY_STRETCH = 3;

    private float widthScale = 0.5f;
    private int width = 0;
    private int height = 0;
    private int color = 0;
    private int animationDuration = 0;
    private int margin = 0;
    private int gravity = 0;
    private boolean transitionScroll;

    public abstract Drawable getIndicator();

    public int getGravity() {
        return gravity;
    }

    public Indicator setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public float getWidthScale() {
        return widthScale;
    }

    public Indicator setWidthScale(float widthScale) {
        this.widthScale = widthScale;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Indicator setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Indicator setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getColor() {
        return color;
    }

    public Indicator setColor(int color) {
        this.color = color;
        return this;
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    public Indicator setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
        return this;
    }

    public int getMargin() {
        return margin;
    }

    public Indicator setMargin(int margin) {
        this.margin = margin;
        return this;
    }

    public boolean isTransitionScroll() {
        return transitionScroll;
    }

    public Indicator setTransitionScroll(boolean transitionScroll) {
        this.transitionScroll = transitionScroll;
        return this;
    }

    public boolean isFullWidth() {
        return !(widthScale > 0 || width > 0);
    }
}
