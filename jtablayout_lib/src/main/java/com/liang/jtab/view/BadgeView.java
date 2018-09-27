package com.liang.jtab.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import com.liang.jtab.R;
import com.liang.jtab.utils.DensityUtils;

public class BadgeView extends android.support.v7.widget.AppCompatTextView {

    private static final int DEF_STROKE = 2;

    private GradientDrawable gradientDrawable;

    private int stroke = DEF_STROKE;

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
        gradientDrawable.setStroke(2, Color.WHITE);

        setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        setText("");
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);
        setPadding(DensityUtils.dip2px(getContext(), 3), 0,
                DensityUtils.dip2px(getContext(), 3), 0);
    }

    /**
     * 设置BadgeView的背景颜色
     *
     * @param color
     */
    public void setBackgroundColor(@ColorInt int color) {
        gradientDrawable.setColor(color);
        setBackgroundDrawable(gradientDrawable);
    }

    /**
     * 设置BadgeView的边框
     *
     * @param width 边框宽度
     * @param color 边框颜色
     */
    public void setStroke(int width, @ColorInt int color) {
        stroke = width;
        gradientDrawable.setStroke(width, color);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (getText().toString().isEmpty()) {
            setMeasuredDimension(DensityUtils.dip2px(getContext(), 10), DensityUtils.dip2px(getContext(), 10));
            return;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) (getPaint().measureText(getText().toString()) + getPaddingLeft() + getPaddingRight()) + stroke * 2;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
            int value = (int) Math.ceil(Math.abs((fontMetrics.bottom - fontMetrics.top)));
            height = value + getPaddingTop() + getPaddingBottom() + stroke * 2;
        }

        if (height > width) {
            int max = Math.max(width, height);
            int measureSpec = MeasureSpec.makeMeasureSpec(max, MeasureSpec.EXACTLY);
            setMeasuredDimension(measureSpec, measureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        gradientDrawable.setCornerRadius(getHeight() / 2);
        gradientDrawable.setBounds(0, 0, getWidth(), getHeight());
        setBackgroundDrawable(gradientDrawable);
    }

    /**
     * 显示BadgeView
     *
     * @param msg
     */
    public void show(String msg) {

        setText(msg);

        if (getVisibility() == VISIBLE) {
            return;
        }
        setVisibility(VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.badge_view_show);
        animation.setInterpolator(new OvershootInterpolator());
        startAnimation(animation);
    }

    /**
     * 隐藏BadgeView
     *
     */
    public void hide() {
        if (getVisibility() == GONE) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.badge_view_hide);
        startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
