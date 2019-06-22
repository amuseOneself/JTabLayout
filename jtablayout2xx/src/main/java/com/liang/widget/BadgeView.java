package com.liang.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.liang.jtablayout.badge.Badge;
import com.liang.jtablayoutx.R;


public class BadgeView extends android.support.v7.widget.AppCompatTextView implements Badge {

    private int padding = 1;

    private int mStroke;
    private int mStrokeColor;
    private int mBackgroundColor;
    private boolean mInitBackgroundFlag;
    private OnDragListener mOnDragListener;
    private boolean canDrag = false;

    public OnDragListener getDragListener() {
        return mOnDragListener;
    }

    public void setDragListener(OnDragListener onDragListener) {
        this.mOnDragListener = onDragListener;
    }

    public interface OnDragListener {
        void onDragOut();
    }

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeView,
                defStyleAttr, 0);
        mStrokeColor = typedArray.getColor(R.styleable.BadgeView_badgeStrokeColor, Color.WHITE);
        mStroke = typedArray.getDimensionPixelSize(R.styleable.BadgeView_badgeStrokeWidth, dip2px(getContext(), 1));
        mBackgroundColor = typedArray.getColor(R.styleable.BadgeView_badgeBackgroundColor, Color.RED);
        typedArray.recycle();
        initBadge();
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    private void initBadge() {
        setGravity(Gravity.CENTER);
        setVisibility(GONE);
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
        final int defPadding = dip2px(getContext(), padding);
        final int length = getText().length();

        if (length == 1) {
            int mix = width - height;
            int ipa = (int) Math.floor(mix / 2.0f);
            if (mix < 0) {
                setPadding(defPadding - ipa, defPadding, defPadding - ipa, defPadding);
            } else {
                setPadding(Math.max(getPaddingLeft(), defPadding), defPadding, Math.max(getPaddingRight(), defPadding), defPadding);
            }
        }

        if (length > 1) {
            setPadding((int) (defPadding + getTextSize() / 2), defPadding, (int) (defPadding + getTextSize() / 2), defPadding);
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
            final int pointWidth = dip2px(getContext(), 10);
            setMeasuredDimension(pointWidth, pointWidth);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 显示BadgeView
     *
     * @param msg
     */
    @Override
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
    @Override
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

    @Override
    public void setBadgeTextSize(float sp) {
        setTextSize(sp);
    }

    @Override
    public void setBadgeBackgroundColor(int color) {
        mBackgroundColor = color;
        setBackgroundDrawable(createStateListDrawable());
    }

    @Override
    public void setBadgeStroke(int width, int color) {
        mStroke = width;
        mStrokeColor = color;
        setBackgroundDrawable(createStateListDrawable());
    }

    @Override
    public void setBadgeTextColor(int color) {
        setTextColor(color);
    }

    public int dip2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    private ViewGroup scrollParent;
    private int[] p = new int[2];
    private int x, y, r;
    private DragView dragView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canDrag) {
            return false;
        }

        ViewGroup root = (ViewGroup) getRootView();
        if (root == null) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                root.getLocationOnScreen(p);
                scrollParent = getScrollParent();
                if (scrollParent != null) {
                    scrollParent.requestDisallowInterceptTouchEvent(true);
                }
                int location[] = new int[2];
                getLocationOnScreen(location);
                x = location[0] + (getWidth() / 2) - p[0];
                y = location[1] + (getHeight() / 2) - p[1];
                r = (getWidth() + getHeight()) / 4;
                dragView = new DragView(getContext());
                dragView.setLayoutParams(new ViewGroup.LayoutParams(root.getWidth(), root.getHeight()));
                setDrawingCacheEnabled(true);
                dragView.catchBitmap = getDrawingCache();
                dragView.setLocation(x, y, r, event.getRawX() - p[0], event.getRawY() - p[1]);
                root.addView(dragView);
                setVisibility(View.INVISIBLE);
                break;
            case MotionEvent.ACTION_MOVE:
                dragView.refrashXY(event.getRawX() - p[0], event.getRawY() - p[1]);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (scrollParent != null) {
                    scrollParent.requestDisallowInterceptTouchEvent(false);
                }
                if (!dragView.broken) { // 没有拉断
                    dragView.cancel();
                } else if (dragView.nearby) {// 拉断了,但是又回去了
                    dragView.cancel();
                } else { // 彻底拉断了
                    dragView.broken();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private ViewGroup getScrollParent() {
        View p = this;
        while (true) {
            View v;
            try {
                v = (View) p.getParent();
            } catch (ClassCastException e) {
                return null;
            }
            if (v == null)
                return null;
            if (v instanceof AbsListView || v instanceof ScrollView || v instanceof ViewPager) {
                return (ViewGroup) v;
            }
            p = v;
        }
    }

    class DragView extends View {
        private Bitmap catchBitmap;
        private Circle c1;
        private Circle c2;
        private Paint paint;
        private Path path = new Path();
        private int maxDistance = 8; // 10倍半径距离视为拉断
        private boolean broken; // 是否拉断过
        private boolean out; // 放手的时候是否拉断
        private boolean nearby;
        private int brokenProgress;

        public DragView(Context context) {
            super(context);
            init();
        }

        public void init() {
            paint = new Paint();
            paint.setColor(mBackgroundColor);
            paint.setAntiAlias(true);
            setBackgroundColor(Color.YELLOW);
        }

        public void setLocation(float c1X, float c1Y, float r, float endX, float endY) {
            broken = false;
            c1 = new Circle(c1X, c1Y, r);
            c2 = new Circle(endX, endY, r);
        }

        public void refrashXY(float x, float y) {
            c2.x = x;
            c2.y = y;
            // 以前的半径应该根据距离缩小点了
            // 计算出距离
            double distance = c1.getDistance(c2);
            int rate = 10;
            c1.r = (float) ((c2.r * c2.r * rate) / (distance + (c2.r * rate)));
            Log.i("info", "c1: " + c1.r);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.TRANSPARENT);
            if (out) {
                float dr = c2.r / 2 + c2.r * 4 * (brokenProgress / 100f);
                Log.i("info", "dr" + dr);
                canvas.drawCircle(c2.x, c2.y, c2.r / (brokenProgress + 1), paint);
                canvas.drawCircle(c2.x - dr, c2.y - dr, c2.r / (brokenProgress + 2), paint);
                canvas.drawCircle(c2.x + dr, c2.y - dr, c2.r / (brokenProgress + 2), paint);
                canvas.drawCircle(c2.x - dr, c2.y + dr, c2.r / (brokenProgress + 2), paint);
                canvas.drawCircle(c2.x + dr, c2.y + dr, c2.r / (brokenProgress + 2), paint);
            } else {
                // 绘制手指跟踪的圆形
                if (catchBitmap == null || (catchBitmap != null && catchBitmap.isRecycled())) {
                    return;
                }
                canvas.drawBitmap(catchBitmap, c2.x - c2.r, c2.y - c2.r, paint);
                path.reset();
                float deltaX = c2.x - c1.x;
                float deltaY = -(c2.y - c1.y);
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                double sin = deltaY / distance;
                double cos = deltaX / distance;
                nearby = distance < c2.r * maxDistance;
                if (nearby && !broken) {
                    canvas.drawCircle(c1.x, c1.y, c1.r, paint);
                    path.moveTo((float) (c1.x - c1.r * sin), (float) (c1.y - c1.r * cos));
                    path.lineTo((float) (c1.x + c1.r * sin), (float) (c1.y + c1.r * cos));
                    path.quadTo((c1.x + c2.x) / 2, (c1.y + c2.y) / 2, (float) (c2.x + c2.r * sin), (float) (c2.y + c2.r
                            * cos));
                    path.lineTo((float) (c2.x - c2.r * sin), (float) (c2.y - c2.r * cos));
                    path.quadTo((c1.x + c2.x) / 2, (c1.y + c2.y) / 2, (float) (c1.x - c1.r * sin), (float) (c1.y - c1.r
                            * cos));
                    canvas.drawPath(path, paint);
                } else {
                    broken = true; // 已经拉断了
                }
            }

        }

        public void cancel() {
            int duration = 150;
            AnimatorSet set = new AnimatorSet();
            ValueAnimator animx = ValueAnimator.ofFloat(c2.x, c1.x);
            animx.setDuration(duration);
            animx.setInterpolator(new OvershootInterpolator(2));
            animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    c2.x = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            ValueAnimator animy = ValueAnimator.ofFloat(c2.y, c1.y);
            animy.setDuration(duration);
            animy.setInterpolator(new OvershootInterpolator(2));
            animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    c2.y = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            set.playTogether(animx, animy);
            set.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewGroup vg = (ViewGroup) DragView.this.getParent();
                    vg.removeView(DragView.this);
                    BadgeView.this.setVisibility(View.VISIBLE);
                }
            });
            set.start();

        }

        public void broken() {
            out = true;
            int duration = 500;
            ValueAnimator a = ValueAnimator.ofInt(0, 100);
            a.setDuration(duration);
            a.setInterpolator(new LinearInterpolator());
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    brokenProgress = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            a.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewGroup vg = (ViewGroup) DragView.this.getParent();
                    vg.removeView(DragView.this);
                }
            });
            a.start();
            if (mOnDragListener != null) {
                mOnDragListener.onDragOut();
            }
        }

        class Circle {
            float x;
            float y;
            float r;

            public Circle(float x, float y, float r) {
                this.x = x;
                this.y = y;
                this.r = r;
            }

            public double getDistance(Circle c) {
                float deltaX = x - c.x;
                float deltaY = y - c.y;
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                return distance;
            }
        }

    }

}
