package com.liang.jtab.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.nfc.Tag;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import com.liang.jtab.R;
import com.liang.jtab.utils.DensityUtils;

import static android.content.ContentValues.TAG;

public class BadgeView extends android.support.v7.widget.AppCompatTextView {

    private static final int DEF_PADDING = 2;
    private int mStroke;
    private int mStrokeColor;
    private int mBackgroundColor;
    private boolean mInitBackgroundFlag;

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JTabLayout,
                defStyleAttr, 0);
        mStrokeColor = typedArray.getColor(R.styleable.BadgeView_strokeColor, Color.WHITE);
        mStroke = typedArray.getDimensionPixelSize(R.styleable.BadgeView_strokeWidth, DensityUtils.dip2px(getContext(), 2));
        mBackgroundColor = typedArray.getColor(R.styleable.BadgeView_backgroundColor, Color.RED);
        initBadge();
    }

    private void initBadge() {
        setGravity(Gravity.CENTER);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!mInitBackgroundFlag) {
                    setBackgroundDrawable(createStateListDrawable());
                    refreshPadding();
                    mInitBackgroundFlag = true;
                    return false;
                }
                return true;
            }
        });
    }

    private void refreshPadding() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        final int defPadding = DensityUtils.dip2px(getContext(), DEF_PADDING);
        if (width > height) {
            final int defLFPadding = DensityUtils.dip2px(getContext(), DEF_PADDING * 3);
            setPadding(defLFPadding, defPadding, defLFPadding, defPadding);
        } else {
            setPadding(defPadding, defPadding, defPadding, defPadding);
        }
    }

    /**
     * Setting the background color of BadgeView
     *
     * @param color
     */
    @Override
    public void setBackgroundColor(@ColorInt int color) {
        mBackgroundColor = color;
        setBackgroundDrawable(createStateListDrawable());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mInitBackgroundFlag = false;
    }

    /**
     * Setting the border of BadgeView
     *
     * @param width
     * @param color
     */
    public void setStroke(int width, @ColorInt int color) {
        mStroke = width;
        mStrokeColor = color;
        setBackgroundDrawable(createStateListDrawable());
    }

    private StateListDrawable createStateListDrawable() {
        StateListDrawable bg = new StateListDrawable();
        GradientDrawable gradientStateNormal = new GradientDrawable();
        gradientStateNormal.setColor(mBackgroundColor);
        gradientStateNormal.setShape(GradientDrawable.RECTANGLE);
        gradientStateNormal.setCornerRadius(50);
        gradientStateNormal.setStroke(mStroke, mStrokeColor);
        bg.addState(View.EMPTY_STATE_SET, gradientStateNormal);
        return bg;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getText().toString().trim().isEmpty()) {
            final int pointWidth = DensityUtils.dip2px(getContext(), 10);
            setMeasuredDimension(pointWidth, pointWidth);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (height > width) {
            setMeasuredDimension(height, height);
        }
//        Log.e(TAG, "onMeasure: ");
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
