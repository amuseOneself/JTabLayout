package com.liang.jtab;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.liang.jtab.indicator.Indicator;
import com.liang.jtab.view.Tab;
import com.liang.jtab.view.TabView;
import com.liang.jtab.listener.OnTabSelectedListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;


public class JTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener,
        ViewPager.OnAdapterChangeListener {

    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;

    private static final int ANIMATION_DURATION = 300;

    private SlidingTabStrip tabStrip;
    private int mode;

    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;
    private PagerAdapterObserver adapterObserver;

    private List<Tab> tabViews;
    private Tab selectedTab;

    private int tabPaddingStart = 10;
    private int tabPaddingTop = 0;
    private int tabPaddingEnd = 10;
    private int tabPaddingBottom = 0;

    private ColorStateList tabTextColors;

    private ValueAnimator scrollAnimator;

    private ArrayList<OnTabSelectedListener> tabSelectedListeners = new ArrayList<>();


    private int dividerWidth;

    private boolean autoRefresh;

    private boolean autoScroll;
    private int scrollState;
    private int previousScrollState;

    public JTabLayout(Context context) {
        this(context, null);
    }

    public JTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tabViews = new ArrayList<>();
        tabStrip = new SlidingTabStrip(context);
        addView(tabStrip, 0, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JTabLayout,
                defStyleAttr, 0);

        if (typedArray.hasValue(R.styleable.JTabLayout_tabTextColor)) {
            tabTextColors = typedArray.getColorStateList(R.styleable.JTabLayout_tabTextColor);
        }

        mode = typedArray.getInt(R.styleable.JTabLayout_tabMode, MODE_FIXED);
        dividerWidth = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_dividerWidth, 0);
        int dividerHeight = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_dividerHeight, 50);
        int dividerColor = typedArray.getColor(R.styleable.JTabLayout_dividerColor, Color.BLACK);

        typedArray.recycle();

        tabStrip.setDividerWidth(dividerWidth);
        tabStrip.setDividerHeight(dividerHeight);
        tabStrip.setDividerColor(dividerColor);
    }

    public void addOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        if (!tabSelectedListeners.contains(listener)) {
            tabSelectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        tabSelectedListeners.remove(listener);
    }

    private void dispatchTabSelected(Tab tab) {

        selectedTab = tab;

        if (tab == null) {
            return;
        }

        for (int i = tabSelectedListeners.size() - 1; i >= 0; i--) {
            tabSelectedListeners.get(i).onTabSelected(tab.getPosition());
        }
    }

    private void dispatchTabUnselected(Tab tab) {
        if (tab == null) {
            return;
        }
        for (int i = tabSelectedListeners.size() - 1; i >= 0; i--) {
            tabSelectedListeners.get(i).onTabUnselected(tab.getPosition());
        }
    }

    private void dispatchTabReselected(Tab tab) {
        if (tab == null) {
            return;
        }
        for (int i = tabSelectedListeners.size() - 1; i >= 0; i--) {
            tabSelectedListeners.get(i).onTabReselected(tab.getPosition());
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
        updateTabViews(true);
    }

    public void setTabPadding(int mTabPaddingStart, int mTabPaddingTop, int mTabPaddingEnd, int mTabPaddingBottom) {
        this.tabPaddingStart = mTabPaddingStart;
        this.tabPaddingTop = mTabPaddingTop;
        this.tabPaddingEnd = mTabPaddingEnd;
        this.tabPaddingBottom = mTabPaddingBottom;
        updateTabViews(true);
    }

    public void setTabTextColors(ColorStateList tabTextColors) {
        this.tabTextColors = tabTextColors;
        updateTabViews(true);
    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
        tabStrip.setDividerWidth(dividerWidth);
        updateTabViews(true);
    }

    public void setDividerHeight(int dividerHeight) {
        tabStrip.setDividerHeight(dividerHeight);
        updateTabViews(true);
    }

    public void setDividerColor(int dividerColor) {
        tabStrip.setDividerColor(dividerColor);
        updateTabViews(true);
    }

    public void updateTabViews(boolean requestLayout) {
        switch (mode) {
            case MODE_FIXED:
                tabStrip.setGravity(Gravity.CENTER);
                break;
            case MODE_SCROLLABLE:
                tabStrip.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;
        }
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            Tab child = (Tab) tabStrip.getChildAt(i);
            child.setPadding(tabPaddingStart, tabPaddingTop, tabPaddingEnd, tabPaddingBottom);
            if (child.getTitleColor() != null && tabTextColors != null) {
                child.setTitleColor(tabTextColors);
            }
            updateTabViewLayoutParams((LinearLayout.LayoutParams) child.getLayoutParams(), i == 0 ? 0 : dividerWidth);
            if (requestLayout) {
                child.requestLayout();
            }
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

    public Tab newTab() {
        return new TabView(getContext());
    }

    public void setIndicator(Indicator indicator) {
        tabStrip.setIndicator(indicator);
    }

    public void setupWithViewPager(ViewPager viewPager) {
        setupWithViewPager(viewPager, true);
    }

    public void setupWithViewPager(ViewPager viewPager, boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        if (this.viewPager != null) {
            this.viewPager.removeOnPageChangeListener(this);
            this.viewPager.removeOnAdapterChangeListener(this);
        }

        if (viewPager != null) {
            this.viewPager = viewPager;
            viewPager.addOnPageChangeListener(this);
            viewPager.addOnAdapterChangeListener(this);
        }

        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter != null) {
            setPagerAdapter(adapter, autoRefresh);
        }
    }

    private void setPagerAdapter(PagerAdapter adapter, boolean autoRefresh) {

        if (mPagerAdapter != null && adapterObserver != null) {
            mPagerAdapter.unregisterDataSetObserver(adapterObserver);
        }

        mPagerAdapter = viewPager.getAdapter();

        if (autoRefresh && adapter != null) {
            if (adapterObserver == null) {
                adapterObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(adapterObserver);
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

            if (viewPager != null && adapterCount > 0) {
                final int curItem = viewPager.getCurrentItem();
                if (curItem != getSelectedTabPosition() && curItem < getTabCount()) {
                    selectTab(getTabAt(curItem));
                    animateToTab(getTabAt(curItem).getPosition());
                }
            }
        }
    }

    private void selectTab(Tab tab) {
        if (selectedTab != null) {
            selectedTab.setSelected(false);
            dispatchTabUnselected(selectedTab);
        }

        tab.setSelected(true);

        dispatchTabSelected(tab);

    }

    private void setViewPageCurrent(Tab tab) {
        if (viewPager != null) {
            viewPager.setCurrentItem(tab.getPosition(), true);
        }
    }

    public SlidingTabStrip getTabStrip() {
        return tabStrip;
    }

    public int getTabCount() {
        return tabViews.size();
    }

    @Nullable
    public Tab getTabAt(int index) {
        return (index < 0 || index >= getTabCount()) ? null : tabViews.get(index);
    }

    public int getSelectedTabPosition() {
        return selectedTab != null ? selectedTab.getPosition() : -1;
    }

    public void addTab(Tab tab) {
        addTab(tab, tabViews.isEmpty());
    }

    public void addTab(Tab tab, int position) {
        addTab(tab, position, tabViews.isEmpty());
    }

    public void addTab(Tab tab, boolean setSelected) {
        addTab(tab, tabViews.size(), setSelected);
    }

    public void addTab(Tab tab, int position, boolean setSelected) {
        configureTab(tab, position);
        addTabView(tab);

        if (setSelected) {
            selectTab(tab);
            animateToTab(tab.getPosition());
        }
    }

    private void configureTab(Tab tab, int position) {

        tab.setPosition(position);

        tabViews.add(position, tab);

        final int count = tabViews.size();
        for (int i = position + 1; i < count; i++) {
            tabViews.get(i).setPosition(i);
        }
    }

    private void addTabView(Tab tab) {
        Log.e("addTabView", "tab: ..." + tab.getPosition());

        tab.setPadding(tabPaddingStart, tabPaddingTop, tabPaddingEnd, tabPaddingBottom);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        updateTabViewLayoutParams(params, tab.getPosition() == 0 ? 0 : dividerWidth);
        tabStrip.addView(tab, tab.getPosition(), params);

        if (tab.getTitleColor() != null && tabTextColors != null) {
            tab.setTitleColor(tabTextColors);
        }

        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                animateToTab(((Tab) v).getPosition());

                if (getSelectedTabPosition() == ((Tab) v).getPosition()) {
                    dispatchTabReselected((Tab) v);
                } else {
                    setViewPageCurrent((Tab) v);
                    selectTab((Tab) v);
                }
            }
        });
    }

    @Override
    public void addView(View child) {
        addViewInternal(child);
    }

    @Override
    public void addView(View child, int index) {
        addViewInternal(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    private void addViewInternal(final View child) {
        if (child instanceof Tab) {
            addTab((Tab) child);
        } else {
            throw new IllegalArgumentException("Only TabItem instances can be added to TabLayout");
        }
    }

    public void removeTab(Tab tab) {
        removeTabAt(tab.getPosition());
    }

    public void removeTabAt(int position) {
        final int selectedTabPosition = selectedTab != null ? selectedTab.getPosition() : 0;
        removeTabViewAt(position);
        tabViews.remove(position);
        final int newTabCount = tabViews.size();
        for (int i = position; i < newTabCount; i++) {
            tabViews.get(i).setPosition(i);
        }

        if (selectedTabPosition == position) {
            selectTab(tabViews.isEmpty() ? null : tabViews.get(Math.max(0, position - 1)));
        }
    }

    private void removeTabViewAt(int position) {
        tabStrip.removeViewAt(position);
        requestLayout();
    }

    public void removeAllTabs() {
        tabStrip.removeAllViews();
        tabViews.clear();
        selectedTab = null;
        requestLayout();
    }

    private void animateToTab(int newPosition) {
        if (newPosition == TabView.INVALID_POSITION || mode == MODE_FIXED) {
            return;
        }

        final int startScrollX = getScrollX();
        final int targetScrollX = calculateScrollXForTab(newPosition, 0);

        if (startScrollX != targetScrollX) {
            ensureScrollAnimator();
            scrollAnimator.setIntValues(startScrollX, targetScrollX);
            scrollAnimator.start();
        }

        tabStrip.animateIndicatorToPosition(newPosition, ANIMATION_DURATION);
    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        if (mode == MODE_SCROLLABLE) {
            final View selectedChild = tabStrip.getChildAt(position);
            final View nextChild = position + 1 < tabStrip.getChildCount()
                    ? tabStrip.getChildAt(position + 1)
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
        if (scrollAnimator == null) {
            scrollAnimator = new ValueAnimator();
            scrollAnimator.setInterpolator(new FastOutSlowInInterpolator());
            scrollAnimator.setDuration(ANIMATION_DURATION);
            scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    scrollTo((int) animator.getAnimatedValue(), 0);
                }
            });
        }
    }

    public void setScrollPosition(int position, float positionOffset) {
        final int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= tabStrip.getChildCount()) {
            return;
        }

        tabStrip.setIndicatorPositionFromTabPosition(position, positionOffset);

        if (scrollAnimator != null && scrollAnimator.isRunning()) {
            scrollAnimator.cancel();
        }
        scrollTo(calculateScrollXForTab(position, positionOffset), 0);

        // Update the 'selected state' view as we scroll, if enabled
//        if (updateSelectedText) {
//            setSelectedTabView(roundedPosition);
//        }
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
            selectTab((Tab) tabStrip.getChildAt(position));
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
        if (this.viewPager == viewPager) {
            setPagerAdapter(newAdapter, autoRefresh);
        }
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

}
