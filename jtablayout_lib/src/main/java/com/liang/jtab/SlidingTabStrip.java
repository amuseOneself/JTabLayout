package com.liang.jtab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SlidingTabStrip extends LinearLayout {

    private int dividerWidth;
    private int dividerHeight;
    private int dividerColor;
    private Paint dividerPaint;

    public SlidingTabStrip(Context context) {
        super(context);
        setWillNotDraw(false);
        dividerPaint = new Paint();
    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
    }

    public void setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dividerWidth > 0 && getChildCount() > 0) {
            dividerPaint.setStrokeWidth(dividerWidth);
            dividerPaint.setColor(dividerColor);
            for (int i = 0; i < getChildCount() - 1; i++) {
                View tab = getChildAt(i);
                canvas.drawLine(tab.getRight() + dividerWidth / 2, (getHeight() - dividerHeight) / 2, tab.getRight() + dividerWidth / 2, (getHeight() - dividerHeight) / 2 + dividerHeight, dividerPaint);
            }
        }
    }

}
