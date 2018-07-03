package com.liang.jtab;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import static android.content.ContentValues.TAG;


public class JTabLayout extends HorizontalScrollView {

    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;
    private SlidingTabStrip tabStrip;
    private int mode = MODE_FIXED;

    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;

    public JTabLayout(Context context) {
        this(context, null);
    }

    public JTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tabStrip = new SlidingTabStrip(context);
        addView(tabStrip, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
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

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        mPagerAdapter = viewPager.getAdapter();
        mPagerAdapter.registerDataSetObserver(new PagerAdapterObserver());
    }


    public SlidingTabStrip getTabStrip() {
        return tabStrip;
    }


    public void addTab(View view) {
        tabStrip.addView(view, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
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

    void populateFromPagerAdapter() {
        if (mPagerAdapter != null) {
            final int adapterCount = mPagerAdapter.getCount();
            Log.e(TAG, "populateFromPagerAdapter adapterCount: ..." + adapterCount);
            for (int i = 0; i < adapterCount; i++) {
                addTab(LayoutInflater.from(getContext()).inflate(R.layout.flash_one, null));
            }

            // Make sure we reflect the currently set ViewPager item
//            if (viewPager != null && adapterCount > 0) {
//                final int curItem = viewPager.getCurrentItem();
//                if (curItem != getSelectedTabPosition() && curItem < getTabCount()) {
//                    selectTab(getTabAt(curItem));
//                }
//            }
        }
    }

}
