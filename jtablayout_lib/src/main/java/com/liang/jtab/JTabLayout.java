package com.liang.jtab;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class JTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener,
        ViewPager.OnAdapterChangeListener {

    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;

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
    private int dividerHeight;
    private int dividerColor;
    private Paint dividerPaint;


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
        dividerHeight = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_dividerHeight, 50);
        dividerColor = typedArray.getColor(R.styleable.JTabLayout_dividerColor, Color.BLACK);

        typedArray.recycle();
    }

    public void addOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        if (!tabSelectedListeners.contains(listener)) {
            tabSelectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        tabSelectedListeners.remove(listener);
    }

    public void dispatchTabSelected(Tab tab) {
        if (tab == null) {
            return;
        }
        for (int i = tabSelectedListeners.size() - 1; i >= 0; i--) {
            tabSelectedListeners.get(i).onTabSelected(tab.getPosition());
        }
    }

    public void dispatchTabUnselected(Tab tab) {
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
            View child = tabStrip.getChildAt(i);
            child.setPadding(tabPaddingStart, tabPaddingTop, tabPaddingEnd, tabPaddingBottom);
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

    public void setViewPager(ViewPager viewPager) {
        setViewPager(viewPager, true);
    }

    public void setViewPager(ViewPager viewPager, boolean autoRefresh) {

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
            if (mPagerAdapter != null && adapterObserver != null) {
                mPagerAdapter.unregisterDataSetObserver(adapterObserver);
            }

            mPagerAdapter = viewPager.getAdapter();

            if (autoRefresh) {
                if (adapterObserver == null) {
                    adapterObserver = new PagerAdapterObserver();
                }
                adapter.registerDataSetObserver(adapterObserver);
                populateFromPagerAdapter();
            }
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

        selectedTab = tab;
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

        LinearLayout.LayoutParams params = mode == MODE_SCROLLABLE ? new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT) :
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        tabStrip.addView(tab, tab.getPosition(), params);

        if (tab.getTitleColor() != null && tabTextColors != null) {
            tab.setTitleColor(tabTextColors);
        }

        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSelectedTabPosition() == ((Tab) v).getPosition()) {
                    dispatchTabReselected((Tab) v);
                } else {
                    selectTab((Tab) v);
                }
            }
        });
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

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
