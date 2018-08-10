package com.liang.jtab.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.liang.jtab.R;

public class BadgeView extends android.support.v7.widget.AppCompatTextView {

    private int radius;

    private GradientDrawable gradientDrawable;

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.RED);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setStroke(3, Color.GRAY);

        setTextSize(10);
        setText("999");
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);
        setPadding(10, 0, 10, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        radius = Math.min(width, height) / 2;


    }


    @Override
    protected void onDraw(Canvas canvas) {

        gradientDrawable.setCornerRadius(getHeight() / 2);
        gradientDrawable.setBounds(0, 0, getWidth(), getHeight());
        gradientDrawable.draw(canvas);

        super.onDraw(canvas);
    }

    public void show() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.badge_view_show);
        startAnimation(animation);
    }
}
