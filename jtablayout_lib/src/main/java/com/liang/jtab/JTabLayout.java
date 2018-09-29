package com.liang.jtab;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.liang.jtab.indicator.DefIndicatorEvaluator;
import com.liang.jtab.indicator.Indicator;
import com.liang.jtab.indicator.IndicatorPoint;
import com.liang.jtab.indicator.TransitionIndicatorEvaluator;
import com.liang.jtab.utils.ColorUtils;
import com.liang.jtab.utils.DensityUtils;
import com.liang.jtab.view.TabView;
import com.liang.jtab.listener.OnTabSelectedListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;


public class JTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener,
        ViewPager.OnAdapterChangeListener, ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = JTabLayout.class.getSimpleName();
    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;

    private static final int ANIMATION_DURATION = 300;

    private SlidingTabStrip mSlidingTabStrip;
    private int mode;

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private PagerAdapterObserver mAdapterObserver;

    private List<Tab> mTabViews;
    private Tab mSelectedTab;
    private Drawable mTabBackground;

    private Indicator mIndicator;
    private ColorStateList mTabTextColors;

    private ValueAnimator mScrollAnimator;
    private ValueAnimator mIndicatorAnimator;

    private ArrayList<OnTabSelectedListener> mTabSelectedListeners = new ArrayList<>();

    private int dividerWidth;

    private boolean autoRefresh;

    private boolean autoScroll;
    private int scrollState;
    private int previousScrollState;

    private int textTransitionMode;

    private int tabTextSize;

    private int itemLayoutOrientation;

    private int badgeColor;
    private int badgeTextColor;
    private int badgeTextSize;
    private int badgeStrokeColor;
    private int badgeStrokeWidth;

    private boolean textBold;

    private boolean isIndicatorScroll;

    private int tabPaddingLeft = 10;
    private int tabPaddingTop = 0;
    private int tabPaddingRight = 10;
    private int tabPaddingBottom = 0;


    public JTabLayout(Context context) {
        this(context, null);
    }

    public JTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTabViews = new ArrayList<>();
        mSlidingTabStrip = new SlidingTabStrip(context);

        addView(mSlidingTabStrip, 0, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JTabLayout,
                defStyleAttr, 0);

        if (typedArray.hasValue(R.styleable.JTabLayout_tabTextColor)) {
            mTabTextColors = typedArray.getColorStateList(R.styleable.JTabLayout_tabTextColor);
        }

        if (typedArray.hasValue(R.styleable.JTabLayout_tabBackground)) {
            mTabBackground = typedArray.getDrawable(R.styleable.JTabLayout_tabBackground);
        }

        mode = typedArray.getInt(R.styleable.JTabLayout_tabMode, MODE_FIXED);
        textTransitionMode = typedArray.getInt(R.styleable.JTabLayout_textColorTransition, 0);
        itemLayoutOrientation = typedArray.getInt(R.styleable.JTabLayout_itemLayoutOrientation, 0);
        dividerWidth = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_dividerWidth, 0);
        int dividerHeight = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_dividerHeight, 50);
        int dividerColor = typedArray.getColor(R.styleable.JTabLayout_dividerColor, Color.BLACK);

        tabTextSize = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabTextSize, DensityUtils.sp2px(getContext(), 13));
        badgeColor = typedArray.getColor(R.styleable.JTabLayout_badgeColor, Color.RED);
        badgeTextColor = typedArray.getColor(R.styleable.JTabLayout_badgeTextColor, Color.WHITE);
        badgeStrokeColor = typedArray.getColor(R.styleable.JTabLayout_badgeStrokeColor, Color.WHITE);

        badgeTextSize = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_badgeTextSize, DensityUtils.sp2px(getContext(), 10));
        badgeStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_badgeTextSize, 2);

        textBold = typedArray.getBoolean(R.styleable.JTabLayout_textBold, false);

        typedArray.recycle();

        mSlidingTabStrip.setDividerWidth(dividerWidth);
        mSlidingTabStrip.setDividerHeight(dividerHeight);
        mSlidingTabStrip.setDividerColor(dividerColor);
    }


    public void addOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        if (!mTabSelectedListeners.contains(listener)) {
            mTabSelectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        mTabSelectedListeners.remove(listener);
    }

    private void dispatchTabSelected(Tab tab, boolean isCallBack) {
        if (tab == null && isCallBack) {
            return;
        }

        for (int i = mTabSelectedListeners.size() - 1; i >= 0; i--) {
            mTabSelectedListeners.get(i).onTabSelected(tab.getPosition());
        }
    }

    private void dispatchTabReselected(Tab tab) {
        if (tab == null) {
            return;
        }
        for (int i = mTabSelectedListeners.size() - 1; i >= 0; i--) {
            mTabSelectedListeners.get(i).onTabReselected(tab.getPosition());
        }
    }

    /**
     * TabLayout
     *
     * @param mode MODE_SCROLLABLE-可滚动、MODE_FIXED-固定
     */
    public void setMode(int mode) {
        this.mode = mode;
        updateTabViews();
    }

    /**
     * 设置TabItem的排列方式
     *
     * @param itemLayoutOrientation
     */
    public void setItemLayoutOrientation(int itemLayoutOrientation) {
        this.itemLayoutOrientation = itemLayoutOrientation;
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            View child = mSlidingTabStrip.getChildAt(i);
            ((Tab) child).setOrientationMode(itemLayoutOrientation);
        }
        updateTabViews();
    }

    /**
     * TabItem的Padding
     */
    public void setTabPadding(int left, int top, int right, int bottom) {
        this.tabPaddingLeft = left;
        this.tabPaddingTop = top;
        this.tabPaddingRight = right;
        this.tabPaddingBottom = bottom;
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            View child = mSlidingTabStrip.getChildAt(i);
            ((Tab) child).setTabPadding(tabPaddingLeft, tabPaddingTop, tabPaddingRight, tabPaddingBottom);
        }
    }

    /**
     * Tab的标题切换颜色
     *
     * @param defaultColor
     * @param selectedColor
     */
    public void setTabTextColors(@ColorInt int defaultColor, @ColorInt int selectedColor) {
        setTabTextColors(ColorUtils.createColorStateList(defaultColor, selectedColor));
    }


    public void setTabTextColors(ColorStateList tabTextColors) {
        this.mTabTextColors = tabTextColors;
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            View child = mSlidingTabStrip.getChildAt(i);
            if (tabTextColors != null) {
                ((Tab) child).setTitleColor(tabTextColors);
            }
        }
    }

    public void setTabBackground(@DrawableRes int resId) {
        setTabBackground(ContextCompat.getDrawable(getContext(), resId));
    }

    public void setTabBackground(Drawable tabBackground) {
        this.mTabBackground = tabBackground;
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            View child = mSlidingTabStrip.getChildAt(i);
            if (tabBackground != null) {
                ((Tab) child).setBackgroundDraw(tabBackground);
            }
        }
    }

    /**
     * 设置指定TabItem的背景
     *
     * @param position
     * @param resId
     */
    public void setTabItemBackground(int position, @DrawableRes int resId) {
        setTabItemBackground(position, ContextCompat.getDrawable(getContext(), resId));
    }

    /**
     * 设置指定TabItem的背景
     *
     * @param position
     * @param tabBackground
     */
    public void setTabItemBackground(int position, Drawable tabBackground) {
        View child = mSlidingTabStrip.getChildAt(position);
        if (child != null) {
            ((Tab) child).setBackgroundDraw(tabBackground);
        }
    }

    /**
     * 设置TabItem的标题字体大小
     *
     * @param tabTextSize
     */
    public void setTabTextSize(int tabTextSize) {
        this.tabTextSize = DensityUtils.sp2px(getContext(), tabTextSize);
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            View child = mSlidingTabStrip.getChildAt(i);
            ((Tab) child).setTextSize(this.tabTextSize);
        }
    }

    /**
     * 设置TabItem的标题选中时变粗
     *
     * @param textBold
     */
    public void setTextBold(boolean textBold) {
        this.textBold = textBold;
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            View child = mSlidingTabStrip.getChildAt(i);
            ((Tab) child).setBold(textBold);
        }
    }

    /**
     * 设置TabItem的标题颜色切换方式
     *
     * @param textTransitionMode
     */
    public void setTextTransitionMode(int textTransitionMode) {
        this.textTransitionMode = textTransitionMode;
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            View child = mSlidingTabStrip.getChildAt(i);
            ((Tab) child).setTextTransitionMode(textTransitionMode);
        }
    }

    /**
     * 设置Tab的分割线的宽度
     *
     * @param dividerWidth
     */
    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
        mSlidingTabStrip.setDividerWidth(dividerWidth);
        updateTabViews();
    }

    /**
     * 设置Tab的分割线的高度
     *
     * @param dividerHeight
     */
    public void setDividerHeight(int dividerHeight) {
        mSlidingTabStrip.setDividerHeight(dividerHeight);
        updateTabViews();
    }

    /**
     * 设置Tab的分割线的颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(@ColorInt int dividerColor) {
        mSlidingTabStrip.setDividerColor(dividerColor);
        updateTabViews();
    }

    /**
     * 显示Badge
     *
     * @param position
     */
    public void showBadgeMsg(int position) {
        showBadgeMsg(position, "dot", true);
    }

    /**
     * 显示Badge
     *
     * @param position
     * @param count
     */
    public void showBadgeMsg(int position, int count) {
        showBadgeMsg(position, count, false);
    }

    /**
     * 显示Badge
     *
     * @param position
     * @param count
     * @param showDot
     */
    public void showBadgeMsg(int position, int count, boolean showDot) {
        showBadgeMsg(position, count > 0 ? count + "" : "", showDot);
    }

    /**
     * 显示Badge
     *
     * @param position
     * @param msg
     */
    public void showBadgeMsg(int position, String msg) {
        showBadgeMsg(position, msg, false);
    }

    /**
     * 显示Badge
     *
     * @param position
     * @param msg
     * @param showDot
     */
    public void showBadgeMsg(int position, String msg, boolean showDot) {
        Tab tab = (Tab) mSlidingTabStrip.getChildAt(position);
        if (tab != null) {
            if (msg.isEmpty()) {
                tab.hideBadgeMsg();
                return;
            }
            tab.showBadgeMsg(msg, showDot);
        }
    }

    /**
     * 隐藏Badge
     *
     * @param position
     */
    public void hideBadgeMsg(int position) {
        showBadgeMsg(position, "", false);
    }

    /**
     * 设置Badge的字体颜色
     *
     * @param color
     */
    public void setBadgeTextColor(@ColorInt int color) {
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            setBadgeTextColor(i, color);
        }
    }

    /**
     * 设置指定Badge的字体颜色
     *
     * @param position
     * @param color
     */
    public void setBadgeTextColor(int position, @ColorInt int color) {
        Tab tab = (Tab) mSlidingTabStrip.getChildAt(position);
        if (tab != null) {
            tab.setBadgeTextColor(color);
        }
    }

    /**
     * 设置指定Badge的字体大小
     *
     * @param position
     * @param textSize
     */
    public void setBadgeTextSize(int position, int textSize) {
        Tab tab = (Tab) mSlidingTabStrip.getChildAt(position);
        if (tab != null) {
            tab.setBadgeTextSize(DensityUtils.sp2px(getContext(), textSize));
        }
    }

    /**
     * 设置Badge的背景颜色
     *
     * @param color
     */
    public void setBadgeColor(@ColorInt int color) {
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            setBadgeColor(i, color);
        }
    }

    /**
     * 设置指定Badge的背景颜色
     *
     * @param position
     * @param color
     */
    public void setBadgeColor(int position, @ColorInt int color) {
        Tab tab = (Tab) mSlidingTabStrip.getChildAt(position);
        if (tab != null) {
            tab.setBadgeColor(color);
        }
    }

    /**
     * 设置Badge的边框和颜色
     *
     * @param width
     * @param color
     */
    public void setBadgeStroke(int width, @ColorInt int color) {
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            setBadgeStroke(i, width, color);
        }
    }

    /**
     * 设置指定Badge的边框和颜色
     *
     * @param width
     * @param color
     */
    public void setBadgeStroke(int position, int width, @ColorInt int color) {
        Tab tab = (Tab) mSlidingTabStrip.getChildAt(position);
        if (tab != null) {
            tab.setBadgeStroke(width, color);
        }
    }

    private void updateTabViews() {
        isIndicatorScroll = true;
        switch (mode) {
            case MODE_FIXED:
                mSlidingTabStrip.setGravity(Gravity.CENTER);
                break;
            case MODE_SCROLLABLE:
                mSlidingTabStrip.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;
        }
        for (int i = 0; i < mSlidingTabStrip.getChildCount(); i++) {
            View child = mSlidingTabStrip.getChildAt(i);
            updateTabViewLayoutParams((LinearLayout.LayoutParams) child.getLayoutParams(), i == 0 ? 0 : dividerWidth);
        }
    }

    private void updateTabViewLayoutParams(LinearLayout.LayoutParams lp, int dividerWidth) {
        lp.leftMargin = dividerWidth;
        if (mode == MODE_FIXED) {
            lp.width = 0;
            lp.weight = 1.0f;
        } else {
            lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.weight = 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 1) {
            final View child = getChildAt(0);
            boolean remeasure = false;
            switch (mode) {
                case MODE_SCROLLABLE:
                    remeasure = child.getMeasuredWidth() < getMeasuredWidth();
                    break;
                case MODE_FIXED:
                    remeasure = child.getMeasuredWidth() != getMeasuredWidth();
                    break;
            }

            if (remeasure) {
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth(), MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mSelectedTab == null) {
            return;
        }

        if (changed) {
            animateToTab(mSelectedTab.getPosition());
        } else {
            if (isIndicatorScroll) {
                animateToTab(mSelectedTab.getPosition());
            }
        }

        isIndicatorScroll = false;
    }

    public Tab newTab() {
        return new TabView(getContext());
    }


    public void setIndicator(Indicator indicator) {
        this.mIndicator = indicator;
        mIndicatorAnimator = ValueAnimator.ofObject(new DefIndicatorEvaluator(), startPoint, endPoint);
        mIndicatorAnimator.addUpdateListener(this);
    }

    public void setupWithViewPager(ViewPager viewPager) {
        setupWithViewPager(viewPager, true);
    }

    public void setupWithViewPager(ViewPager viewPager, boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        if (this.mViewPager != null) {
            this.mViewPager.removeOnPageChangeListener(this);
            this.mViewPager.removeOnAdapterChangeListener(this);
        }

        if (viewPager == null) {
            return;
        }

        this.mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        viewPager.addOnAdapterChangeListener(this);

        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter != null) {
            setPagerAdapter(adapter, autoRefresh);
        }
    }

    private void setPagerAdapter(PagerAdapter adapter, boolean autoRefresh) {

        if (mPagerAdapter != null && mAdapterObserver != null) {
            mPagerAdapter.unregisterDataSetObserver(mAdapterObserver);
        }

        mPagerAdapter = mViewPager.getAdapter();

        if (autoRefresh && adapter != null) {
            if (mAdapterObserver == null) {
                mAdapterObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(mAdapterObserver);
            populateFromPagerAdapter();
        }
    }

    private void populateFromPagerAdapter() {
        removeAllTabs();
        if (mPagerAdapter != null) {
            final int adapterCount = mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                addTab(newTab().setTitle(mPagerAdapter.getPageTitle(i)), false);
            }

            if (mViewPager != null && adapterCount > 0) {
                final int curItem = mViewPager.getCurrentItem();
                if (curItem != getSelectedTabPosition() && curItem < getTabCount()) {
                    selectTab(getTabAt(curItem), true);
                    animateToTab(getTabAt(curItem).getPosition());
                }
            }
        }
    }

    public void setCurrentItem(int position) {
        Tab tab = (Tab) mSlidingTabStrip.getChildAt(position);
        if (tab != null) {
            selectTab(tab, true);
            animateToTab(tab.getPosition());
        }
    }

    private void selectTab(Tab tab, boolean isCallBack) {
        if (mSelectedTab != null) {
            mSelectedTab.setSelected(false);
        }

        Log.d(TAG, "selectTab: " + tab.getPosition());
        tab.setSelected(true);
        mSelectedTab = tab;
        dispatchTabSelected(tab, isCallBack);
    }

    private void setViewPageCurrent(Tab tab) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(tab.getPosition());
        }
    }

    public SlidingTabStrip getTabStrip() {
        return mSlidingTabStrip;
    }

    public int getTabCount() {
        return mTabViews.size();
    }

    @Nullable
    public Tab getTabAt(int index) {
        return (index < 0 || index >= getTabCount()) ? null : mTabViews.get(index);
    }

    public int getSelectedTabPosition() {
        return mSelectedTab != null ? mSelectedTab.getPosition() : -1;
    }

    public void addTab(Tab tab) {
        addTab(tab, mTabViews.isEmpty());
    }

    public void addTab(Tab tab, int position) {
        addTab(tab, position, mTabViews.isEmpty());
    }

    public void addTab(Tab tab, boolean setSelected) {
        addTab(tab, mTabViews.size(), setSelected);
    }

    public void addTab(Tab tab, int position, boolean setSelected) {
        isIndicatorScroll = true;

        configureTab(tab, position);
        addTabView(tab);

        if (setSelected) {
            selectTab(tab, true);
        }
        Log.d(TAG, "addTab: " + tab.getPosition());
    }

    private void configureTab(Tab tab, int position) {

        tab.setPosition(position);

        mTabViews.add(position, tab);

        final int count = mTabViews.size();
        for (int i = position + 1; i < count; i++) {
            mTabViews.get(i).setPosition(i);
        }
    }

    private void addTabView(Tab tab) {
        tab.setTabPadding(tabPaddingLeft, tabPaddingTop, tabPaddingRight, tabPaddingBottom);
        tab.setOrientationMode(itemLayoutOrientation);
        tab.setTextTransitionMode(textTransitionMode);
        tab.setTextSize(tabTextSize);
        tab.setBold(textBold);
        tab.setBadgeColor(badgeColor);
        tab.setBadgeTextColor(badgeTextColor);
        tab.setBadgeTextSize(badgeTextSize);
        tab.setBadgeStroke(badgeStrokeWidth, badgeStrokeColor);
        if (tab.getTitleColor() == null && mTabTextColors != null) {
            tab.setTitleColor(mTabTextColors);
        }
        if (tab.getBackground() == null && mTabBackground != null) {
            tab.setBackgroundDraw(mTabBackground);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        updateTabViewLayoutParams(params, tab.getPosition() == 0 ? 0 : dividerWidth);
        mSlidingTabStrip.addView(tab.getView(), tab.getPosition(), params);

        tab.getView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                animateToTab(((Tab) v).getPosition());

                if (getSelectedTabPosition() == ((Tab) v).getPosition()) {
                    dispatchTabReselected((Tab) v);
                } else {
                    setViewPageCurrent((Tab) v);
                    selectTab((Tab) v, true);
                }
            }
        });
    }

    public void removeTab(Tab tab) {
        removeTabAt(tab.getPosition());
    }

    public void removeTabAt(int position) {
        final int selectedTabPosition = mSelectedTab != null ? mSelectedTab.getPosition() : 0;
        removeTabViewAt(position);
        mTabViews.remove(position);
        final int newTabCount = mTabViews.size();
        for (int i = position; i < newTabCount; i++) {
            mTabViews.get(i).setPosition(i);
        }

        if (selectedTabPosition == position) {
            selectTab(mTabViews.isEmpty() ? null : mTabViews.get(Math.max(0, position - 1)), true);
        }

        if (mSlidingTabStrip.getChildCount() == 0) {
            mSelectedPosition = 0;
        }
    }

    private void removeTabViewAt(int position) {
        mSlidingTabStrip.removeViewAt(position);
    }

    public void removeAllTabs() {
        mSlidingTabStrip.removeAllViews();
        mTabViews.clear();
        mSelectedTab = null;
        mSelectedPosition = 0;
    }

    private void animateToTab(int newPosition) {

        if (newPosition == TabView.INVALID_POSITION) {
            return;
        }

        if (mode == MODE_SCROLLABLE) {
            final int startScrollX = getScrollX();
            final int targetScrollX = calculateScrollXForTab(newPosition, 0);

            if (startScrollX != targetScrollX) {
                ensureScrollAnimator();
                mScrollAnimator.setIntValues(startScrollX, targetScrollX);
                mScrollAnimator.start();
            }
        }
        animateIndicatorToPosition(newPosition, ANIMATION_DURATION);
    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        if (mode == MODE_SCROLLABLE) {
            final View selectedChild = mSlidingTabStrip.getChildAt(position);
            final View nextChild = position + 1 < mSlidingTabStrip.getChildCount()
                    ? mSlidingTabStrip.getChildAt(position + 1)
                    : null;
            final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
            final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;

            int scrollBase = selectedChild.getLeft() + (selectedWidth / 2) - (getWidth() / 2);

            int scrollOffset = (int) ((selectedWidth + nextWidth + dividerWidth * 2) * 0.5f * positionOffset);

            return (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR)
                    ? scrollBase + scrollOffset
                    : scrollBase - scrollOffset;
        }
        return 0;
    }

    private void ensureScrollAnimator() {
        if (mScrollAnimator == null) {
            mScrollAnimator = new ValueAnimator();
            mScrollAnimator.setInterpolator(new FastOutSlowInInterpolator());
            mScrollAnimator.setDuration(ANIMATION_DURATION);
            mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    scrollTo((int) animator.getAnimatedValue(), 0);
                }
            });
        }
    }

    public void setScrollPosition(int position, float positionOffset) {
        final int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= mSlidingTabStrip.getChildCount()) {
            return;
        }

        setIndicatorPositionFromTabPosition(position, positionOffset);

        if (mScrollAnimator != null && mScrollAnimator.isRunning()) {
            mScrollAnimator.cancel();
        }
        scrollTo(calculateScrollXForTab(position, positionOffset), 0);

        if (positionOffset > 0 && textTransitionMode > 0) {
            if (position + 1 < mSlidingTabStrip.getChildCount()) {
                ((Tab) mSlidingTabStrip.getChildAt(position)).transition(textTransitionMode, 1.0f - positionOffset);
                ((Tab) mSlidingTabStrip.getChildAt(position + 1)).transition(textTransitionMode, positionOffset);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (autoScroll) {
            setScrollPosition(position, positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (autoScroll) {
            selectTab((Tab) mSlidingTabStrip.getChildAt(position), true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        previousScrollState = scrollState;
        scrollState = state;
        if (scrollState == SCROLL_STATE_DRAGGING) {
            autoScroll = true;
        }

        if (scrollState == SCROLL_STATE_IDLE && previousScrollState == SCROLL_STATE_SETTLING) {
            autoScroll = false;
        }

    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
        if (this.mViewPager == viewPager) {
            setPagerAdapter(newAdapter, autoRefresh);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        IndicatorPoint p = (IndicatorPoint) animation.getAnimatedValue();
        mIndicator.left = p.left;
        mIndicator.right = p.right;
        invalidate();
    }

    private class PagerAdapterObserver extends DataSetObserver {
        PagerAdapterObserver() {
        }

        @Override
        public void onChanged() {
            populateFromPagerAdapter();
        }

        @Override
        public void onInvalidated() {
            populateFromPagerAdapter();
        }
    }

    private int mSelectedPosition = -1;
    private IndicatorPoint startPoint = new IndicatorPoint();
    private IndicatorPoint endPoint = new IndicatorPoint();

    private void animateIndicatorToPosition(int newPosition, int duration) {
        if (mIndicator == null) {
            mSelectedPosition = newPosition;
            return;
        }
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }

        if (mIndicator.isTransitionScroll()) {
            mIndicatorAnimator.setEvaluator(new TransitionIndicatorEvaluator());
        } else {
            mIndicatorAnimator.setEvaluator(new DefIndicatorEvaluator());
        }

        View currentTab = mSlidingTabStrip.getChildAt(mSelectedPosition);
        if (currentTab != null && currentTab.getWidth() > 0) {
            int indicatorWidth = getIndicatorWidth(currentTab);
            startPoint.left = currentTab.getLeft() + (currentTab.getWidth() - indicatorWidth) / 2;
            startPoint.right = startPoint.left + indicatorWidth;
        }
        View nextTabView = mSlidingTabStrip.getChildAt(newPosition);
        if (nextTabView != null && nextTabView.getWidth() > 0) {
            int indicatorWidth = getIndicatorWidth(nextTabView);
            endPoint.left = nextTabView.getLeft() + (nextTabView.getWidth() - indicatorWidth) / 2;
            endPoint.right = endPoint.left + indicatorWidth;
        }

        if (endPoint.left == startPoint.left && endPoint.right == startPoint.right) {
            mIndicator.left = endPoint.left;
            mIndicator.right = endPoint.right;
            invalidate();
        } else {
            mIndicatorAnimator.setDuration(duration);
            mIndicatorAnimator.start();
        }
        mSelectedPosition = newPosition;
    }

    private void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
        if (mIndicator == null) {
            return;
        }
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }

        mSelectedPosition = position;
        updateIndicatorPosition(-1, positionOffset);
    }

    private void updateIndicatorPosition(int position, float positionOffset) {
        if (position < 0) {
            position = mSelectedPosition + 1;
        }

        float left = -1, right = -1;
        View currentTab = mSlidingTabStrip.getChildAt(mSelectedPosition);
        if (currentTab != null && currentTab.getWidth() > 0) {
            left = currentTab.getLeft();
            right = currentTab.getRight();
            int indicatorWidth = getIndicatorWidth(currentTab);
            left += (currentTab.getWidth() - indicatorWidth) / 2;
            right = left + indicatorWidth;
        }
        if (position < mSlidingTabStrip.getChildCount()) {
            View nextTabView = mSlidingTabStrip.getChildAt(position);
            int indicatorWidth = getIndicatorWidth(nextTabView);
            float nextLeft = nextTabView.getLeft();
            float nextRight;
            nextLeft += (nextTabView.getWidth() - indicatorWidth) / 2;
            nextRight = nextLeft + indicatorWidth;

            if (mIndicator.isTransitionScroll()) {
                float offR = positionOffset * 2 - 1;
                float offL = positionOffset * 2;

                if (position < mSelectedPosition && positionOffset > 0) {
                    if (offR < 0) {
                        offR = 0;
                    }
                    if (1 - offL < 0) {
                        offL = 1;
                    }
                } else {
                    offL = positionOffset * 2 - 1;
                    offR = positionOffset * 2;
                    if (offL < 0) {
                        offL = 0;
                    }
                    if (1 - offR < 0) {
                        offR = 1;
                    }
                }
                left += ((nextLeft - left) * offL);
                right += ((nextRight - right) * offR);
            } else {
                left += ((nextLeft - left) * positionOffset);
                right += ((nextRight - right) * positionOffset);
            }
        }
        setIndicatorPosition(left, right);
    }

    private int getIndicatorWidth(View tab) {
        if (mIndicator == null) {
            return 0;
        }
        int indicatorWidth = mIndicator.getWidth();
        if (indicatorWidth <= 0) {
            indicatorWidth = (int) (tab.getWidth() * mIndicator.getWidthScale());
        }
        return indicatorWidth;
    }

    private void setIndicatorPosition(float left, float right) {
        if (left != mIndicator.left || right != mIndicator.right) {
            mIndicator.left = left;
            mIndicator.right = right;
            invalidate();
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || mSlidingTabStrip.getChildCount() <= 0) {
            return;
        }

        if (mIndicator != null && mIndicator.left >= 0 && mIndicator.right > mIndicator.left) {
            mIndicator.draw(canvas, getHeight());
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mSelectedTab", mSelectedTab != null ? mSelectedTab.getPosition() : 0);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            int selectedPosition = bundle.getInt("mSelectedTab");
            state = bundle.getParcelable("instanceState");
            Tab tab = (Tab) mSlidingTabStrip.getChildAt(selectedPosition);
            if (tab != null) {
                selectTab(tab, false);
                animateToTab(tab.getPosition());
            }

        }
        super.onRestoreInstanceState(state);
    }
}
