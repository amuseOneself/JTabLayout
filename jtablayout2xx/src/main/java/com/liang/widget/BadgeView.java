package com.liang.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import com.liang.jtablayoutx.R;

public class BadgeView extends android.support.v7.widget.AppCompatTextView {

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int length = getText().toString().trim().length();

        if (length == 0) {
            final int pointWidth = dip2px(getContext(), 10);
            setMeasuredDimension(pointWidth, pointWidth);
            return;
        }

        if (length == 1) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            setMeasuredDimension(Math.max(width, height), Math.max(width, height));
        } else {
            int textSize = (int) getPaint().getTextSize();
            setPadding(textSize / 3, 0, textSize / 3, 0);
        }
        setGravity(Gravity.CENTER);
    }

    public int dip2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 显示BadgeView
     *
     * @param msg
     */
    public void show(String msg) {
        setText(msg.trim());
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
