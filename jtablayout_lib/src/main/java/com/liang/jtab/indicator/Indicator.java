package com.liang.jtab.indicator;

import android.graphics.Canvas;

public abstract class Indicator {
    public float left = 0;
    public float right = 0;
    private float widthScale = 0.5f;
    private int width = -1;
    private int height = 10;

    private boolean transitionScroll;

    public abstract void draw(Canvas canvas, int tabHeight);

    public float getWidthScale() {
        return widthScale;
    }

    /**
     * Set the ratio of the width of Indicator to the width of Tab
     *
     * @param widthScale
     */
    public Indicator setWidthScale(float widthScale) {
        this.widthScale = widthScale;
        return this;
    }

    public int getWidth() {
        return width;
    }

    /**
     * Set the width of Indicator
     *
     * @param width
     */
    public Indicator setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Setting Indicator Height
     *
     * @param height
     */
    public Indicator setHeight(int height) {
        this.height = height;
        return this;
    }

    /**
     * Setting Indicator Mobility
     *
     * @param transitionScroll True is the snake crawl mode false is the default
     */
    public Indicator setTransitionScroll(boolean transitionScroll) {
        this.transitionScroll = transitionScroll;
        return this;
    }

    public boolean isTransitionScroll() {
        return transitionScroll;
    }
}
