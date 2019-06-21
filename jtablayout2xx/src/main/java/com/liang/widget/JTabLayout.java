package com.liang.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pools;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.content.res.AppCompatResources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import com.liang.jtablayout.adapter.TabAdapter;
import com.liang.jtablayout.indicator.DefIndicatorEvaluator;
import com.liang.jtablayout.indicator.IndicatorPoint;
import com.liang.jtablayout.indicator.TabScaleEvaluator;
import com.liang.jtablayout.indicator.TransitionIndicatorEvaluator;
import com.liang.jtablayout.tab.Tab;
import com.liang.jtablayout.tab.TabChild;
import com.liang.jtablayout.tab.TabItem;
import com.liang.jtablayout.utils.ColorUtils;
import com.liang.jtablayout.utils.DensityUtils;
import com.liang.jtablayout.utils.MaterialResources;
import com.liang.jtablayout.utils.ViewUtils;
import com.liang.jtablayoutx.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

@ViewPager.DecorView
public class JTabLayout extends HorizontalScrollView {
    @Dimension(
            unit = 0
    )
    private static final int DEFAULT_HEIGHT_WITH_TEXT_ICON = 72;
    @Dimension(
            unit = 0
    )
    static final int DEFAULT_GAP_TEXT_ICON = 8;
    @Dimension(
            unit = 0
    )
    private static final int DEFAULT_HEIGHT = 48;
    @Dimension(
            unit = 0
    )
    private static final int TAB_MIN_WIDTH_MARGIN = 56;
    @Dimension(
            unit = 0
    )
    private static final int MIN_INDICATOR_WIDTH = 24;
    @Dimension(
            unit = 0
    )
    static final int FIXED_WRAP_GUTTER_MIN = 16;
    private static final int INVALID_WIDTH = -1;
    private static final int ANIMATION_DURATION = 300;
    private static final Pools.Pool<Tab<? extends TabChild>> tabPool = new Pools.SynchronizedPool<>(16);
    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;
    public static final int GRAVITY_FILL = 0;
    public static final int GRAVITY_CENTER = 1;
    public static final int INDICATOR_GRAVITY_BOTTOM = 0;
    public static final int INDICATOR_GRAVITY_CENTER = 1;
    public static final int INDICATOR_GRAVITY_TOP = 2;
    public static final int INDICATOR_GRAVITY_STRETCH = 3;
    private final ArrayList<Tab<TabChild>> tabs;
    private final boolean tabTextBold;
    private Tab<TabChild> selectedTab;
    private final RectF tabViewContentBounds;
    private final SlidingTabIndicator slidingTabIndicator;
    private int tabPaddingStart;
    private int tabPaddingTop;
    private int tabPaddingEnd;
    private int tabPaddingBottom;
    private ColorStateList tabTextColors;
    private ColorStateList tabIconTint;
    private ColorStateList tabRippleColorStateList;
    android.graphics.PorterDuff.Mode tabIconTintMode;
    @Nullable
    Drawable tabSelectedIndicator;
    private int tabTextSize;
    private final int tabBackgroundResId;
    int tabMaxWidth;
    private final int requestedTabMinWidth;
    private final int requestedTabMaxWidth;
    private final int scrollableTabMinWidth;
    private int contentInsetStart;
    int tabGravity;
    int tabIndicatorAnimationDuration;
    int tabIndicatorGravity;
    int mode;
    boolean inlineLabel;
    boolean tabIndicatorFullWidth;
    boolean tabIndicatorTransitionScroll;
    float tabScaleTransitionScroll;
    boolean tabColorTransitionScroll;
    private boolean unboundedRipple;
    private BaseOnTabSelectedListener selectedListener;
    private final ArrayList<BaseOnTabSelectedListener> selectedListeners;
    private BaseOnTabSelectedListener currentVpSelectedListener;
    private ValueAnimator scrollAnimator;
    private ValueAnimator scaleAnimator;
    ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private DataSetObserver pagerAdapterObserver;
    private TabLayoutOnPageChangeListener pageChangeListener;
    private AdapterChangeListener adapterChangeListener;
    private boolean setupViewPagerImplicitly;
    private final Pools.Pool<TabChild> tabViewPool;

    private int tabDividerWidth;
    private int tabDividerHeight;
    private int tabDividerColor;

    int tabIndicatorWidth;
    int tabIndicatorMargin;
    float tabIndicatorWidthScale;

    public static final TimeInterpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
    private int selectedPosition = -1;


    public JTabLayout(Context context) {
        this(context, null);
    }

    public JTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.tabs = new ArrayList();
        this.tabViewContentBounds = new RectF();
        this.tabMaxWidth = Integer.MAX_VALUE;
        this.selectedListeners = new ArrayList();
        this.tabViewPool = new Pools.SimplePool<>(12);
        this.setHorizontalScrollBarEnabled(false);
        this.slidingTabIndicator = new SlidingTabIndicator(context);
        super.addView(this.slidingTabIndicator, 0, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JTabLayout,
                defStyleAttr, 0);

        this.slidingTabIndicator.setSelectedIndicatorHeight(typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabIndicatorHeight, -1));
        this.slidingTabIndicator.setSelectedIndicatorColor(typedArray.getColor(R.styleable.JTabLayout_tabIndicatorColor, 0));
        this.setSelectedTabIndicator(MaterialResources.getDrawable(context, typedArray, R.styleable.JTabLayout_tabIndicator));
        this.setSelectedTabIndicatorGravity(typedArray.getInt(R.styleable.JTabLayout_tabIndicatorGravity, 0));
        this.setTabIndicatorFullWidth(typedArray.getBoolean(R.styleable.JTabLayout_tabIndicatorFullWidth, true));
        this.tabPaddingStart = this.tabPaddingTop = this.tabPaddingEnd = this.tabPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabPadding, 0);
        this.tabPaddingStart = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabPaddingStart, this.tabPaddingStart);
        this.tabPaddingTop = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabPaddingTop, this.tabPaddingTop);
        this.tabPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabPaddingEnd, this.tabPaddingEnd);
        this.tabPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabPaddingBottom, this.tabPaddingBottom);

        this.tabDividerWidth = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabDividerWidth, 0);
        this.tabDividerHeight = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabDividerHeight, -1);
        this.tabDividerColor = typedArray.getColor(R.styleable.JTabLayout_tabDividerColor, 0);

        this.tabIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabIndicatorWidth, 0);
        this.tabIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabIndicatorMargin, 0);
        this.tabIndicatorWidthScale = typedArray.getFloat(R.styleable.JTabLayout_tabIndicatorWidthScale, 0);

        if (typedArray.hasValue(R.styleable.JTabLayout_tabTextColor)) {
            tabTextColors = MaterialResources.getColorStateList(context, typedArray, R.styleable.JTabLayout_tabTextColor);
        }

        this.tabTextSize = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabTextSize, 0);
        this.tabIconTint = MaterialResources.getColorStateList(context, typedArray, R.styleable.JTabLayout_tabIconTint);
        this.tabIconTintMode = ViewUtils.parseTintMode(typedArray.getInt(R.styleable.JTabLayout_tabIconTintMode, -1), (android.graphics.PorterDuff.Mode) null);
        this.tabIndicatorFullWidth = typedArray.getBoolean(R.styleable.JTabLayout_tabIndicatorFullWidth, false);
        this.tabIndicatorTransitionScroll = typedArray.getBoolean(R.styleable.JTabLayout_tabIndicatorTransitionScroll, false);
        this.tabScaleTransitionScroll = typedArray.getFloat(R.styleable.JTabLayout_tabScaleTransitionScroll, 1.0f);
        this.tabColorTransitionScroll = typedArray.getBoolean(R.styleable.JTabLayout_tabTextColorTransitionScroll, false);
        this.tabTextBold = typedArray.getBoolean(R.styleable.JTabLayout_tabTextBold, false);

        this.tabRippleColorStateList = MaterialResources.getColorStateList(context, typedArray, R.styleable.JTabLayout_tabRippleColor);
        this.tabIndicatorAnimationDuration = typedArray.getInt(R.styleable.JTabLayout_tabIndicatorAnimationDuration, 300);
        this.requestedTabMinWidth = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabMinWidth, -1);
        this.requestedTabMaxWidth = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabMaxWidth, -1);
        this.tabBackgroundResId = typedArray.getResourceId(R.styleable.JTabLayout_tabBackground, 0);
        this.contentInsetStart = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_tabContentStart, 0);
        this.mode = typedArray.getInt(R.styleable.JTabLayout_tabMode, 1);
        this.tabGravity = typedArray.getInt(R.styleable.JTabLayout_tabGravity, 0);
        this.inlineLabel = typedArray.getBoolean(R.styleable.JTabLayout_tabInlineLabel, false);
        this.unboundedRipple = typedArray.getBoolean(R.styleable.JTabLayout_tabUnboundedRipple, false);
        typedArray.recycle();

        Resources res = this.getResources();
        this.scrollableTabMinWidth = res.getDimensionPixelSize(R.dimen.design_tab_scrollable_min_width);
        this.applyModeAndGravity();
    }

    public void setSelectedTabIndicatorColor(@ColorInt int color) {
        this.slidingTabIndicator.setSelectedIndicatorColor(color);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setSelectedTabIndicatorHeight(int height) {
        this.slidingTabIndicator.setSelectedIndicatorHeight(height);
    }

    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText) {
        this.setScrollPosition(position, positionOffset, updateSelectedText, true);
    }

    void setScrollPosition(int position, float positionOffset, boolean updateSelectedText, boolean updateIndicatorPosition) {

        int roundedPosition = Math.round((float) position + positionOffset);
        if (roundedPosition >= 0 && roundedPosition < this.slidingTabIndicator.getChildCount()) {
            if (updateIndicatorPosition) {
                this.slidingTabIndicator.setIndicatorPositionFromTabPosition(position, positionOffset);
            }

            if (this.scrollAnimator != null && this.scrollAnimator.isRunning()) {
                this.scrollAnimator.cancel();
            }

            this.scrollTo(this.calculateScrollXForTab(position, positionOffset), 0);

            if (updateSelectedText) {
                this.setSelectedTabView(roundedPosition, true);
            }

            if (positionOffset > 0 && position + 1 < this.slidingTabIndicator.getChildCount()) {
                if (tabScaleTransitionScroll > 1) {
                    float scale = tabScaleTransitionScroll - 1.0f;
                    ((TabChild) slidingTabIndicator.getChildAt(position)).updateScale(1.0f + scale * (1.0f - positionOffset));
                    ((TabChild) slidingTabIndicator.getChildAt(position + 1)).updateScale(1.0f + scale * positionOffset);
                }

                if (tabColorTransitionScroll) {
                    ((TabChild) slidingTabIndicator.getChildAt(position)).updateColor(1.0f - positionOffset);
                    ((TabChild) slidingTabIndicator.getChildAt(position + 1)).updateColor(positionOffset);
                }
            }
        }
    }

    /**
     * Show Badge
     *
     * @param position
     */
    public void showBadgeMsg(int position) {
        showBadgeMsg(position, "", true);
    }

    /**
     * Show Badge
     *
     * @param position
     * @param count
     */
    public void showBadgeMsg(int position, int count) {
        showBadgeMsg(position, count + "", count > 0);
    }

    public void showBadgeMsg(int position, @NonNull String msg) {
        showBadgeMsg(position, msg, msg.trim().length() > 0);
    }

    /**
     * Show Badge
     *
     * @param position
     * @param msg
     * @param showDot
     */
    public void showBadgeMsg(int position, String msg, boolean showDot) {
        TabChild tab = (TabChild) slidingTabIndicator.getChildAt(position);
        if (tab != null) {
            if (showDot) {
                tab.showBadge(msg);
            } else {
                tab.hideBadge();
            }
        }
    }

    /**
     * Setting the font color of Badge
     *
     * @param color
     */
    public void setBadgeTextColor(@ColorInt int color) {
        for (int i = 0; i < slidingTabIndicator.getChildCount(); i++) {
            setBadgeTextColor(i, color);
        }
    }

    /**
     * Set the font color of the specified Badge
     *
     * @param position
     * @param color
     */
    public void setBadgeTextColor(int position, @ColorInt int color) {
        TabChild tab = (TabChild) slidingTabIndicator.getChildAt(position);
        if (tab != null) {
            tab.setBadgeTextColor(color);
        }
    }

    /**
     * Set the font size of the specified Badge
     *
     * @param position
     * @param textSize
     */
    public void setBadgeTextSize(int position, int textSize) {
        TabChild tab = (TabChild) slidingTabIndicator.getChildAt(position);
        if (tab != null) {
            tab.setBadgeTextSize(textSize);
        }
    }

    public void setBadgeTextSize(int textSize) {
        for (int i = 0; i < slidingTabIndicator.getChildCount(); i++) {
            setBadgeTextSize(i, textSize);
        }
    }

    /**
     * Setting the background color of Badge
     *
     * @param color
     */
    public void setBadgeColor(@ColorInt int color) {
        for (int i = 0; i < slidingTabIndicator.getChildCount(); i++) {
            setBadgeColor(i, color);
        }
    }

    /**
     * Set the background color of the specified Badge
     *
     * @param position
     * @param color
     */
    public void setBadgeColor(int position, @ColorInt int color) {
        TabChild tab = (TabChild) slidingTabIndicator.getChildAt(position);
        if (tab != null) {
            tab.setBadgeBackgroundColor(color);
        }
    }

    /**
     * Set the border and color of the Badge
     *
     * @param width
     * @param color
     */
    public void setBadgeStroke(int width, @ColorInt int color) {
        for (int i = 0; i < slidingTabIndicator.getChildCount(); i++) {
            setBadgeStroke(i, width, color);
        }
    }

    /**
     * Set the border and color of the specified Badge
     *
     * @param width
     * @param color
     */
    public void setBadgeStroke(int position, int width, @ColorInt int color) {
        TabChild tab = (TabChild) slidingTabIndicator.getChildAt(position);
        if (tab != null) {
            tab.setBadgeStroke(width, color);
        }
    }

    public void addTab(@NonNull Tab tab) {
        this.addTab(tab, this.tabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, int position) {
        this.addTab(tab, position, this.tabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, boolean setSelected) {
        this.addTab(tab, this.tabs.size(), setSelected);
    }

    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        if (tab.getParent() != this) {
            throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
        } else {
            this.configureTab(tab, position);
            this.addTabView(tab);
            if (setSelected) {
                tab.select();
            }
        }
    }

    private void addTabFromItemView(@NonNull TabItem item) {
        Tab tab = this.newTab();
        if (item.text != null) {
            tab.setText(item.text);
        }

        if (item.icon != null) {
            tab.setIcon(item.icon);
        }

        if (!TextUtils.isEmpty(item.getContentDescription())) {
            tab.setContentDescription(item.getContentDescription());
        }

        this.addTab(tab);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setOnTabSelectedListener(@Nullable BaseOnTabSelectedListener listener) {
        if (this.selectedListener != null) {
            this.removeOnTabSelectedListener(this.selectedListener);
        }

        this.selectedListener = listener;
        if (listener != null) {
            this.addOnTabSelectedListener(listener);
        }
    }

    public void addOnTabSelectedListener(@NonNull BaseOnTabSelectedListener listener) {
        if (!this.selectedListeners.contains(listener)) {
            this.selectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(@NonNull BaseOnTabSelectedListener listener) {
        this.selectedListeners.remove(listener);
    }

    public void clearOnTabSelectedListeners() {
        this.selectedListeners.clear();
    }

    @NonNull
    public Tab newTab() {
        Tab tab = this.createTabFromPool();
        tab.setParent(this);
        tab.setTabItem(this.createTabView(tab));
        return tab;
    }

    protected Tab createTabFromPool() {
        Tab tab = tabPool.acquire();
        if (tab == null) {
            tab = new Tab();
        }
        return tab;
    }

    protected boolean releaseFromTabPool(Tab tab) {
        return tabPool.release(tab);
    }

    public int getTabCount() {
        return this.tabs.size();
    }

    @Nullable
    public Tab getTabAt(int index) {
        return index >= 0 && index < this.getTabCount() ? this.tabs.get(index) : null;
    }

    public int getSelectedTabPosition() {
        return this.selectedTab != null ? this.selectedTab.getPosition() : -1;
    }

    public void removeTab(Tab tab) {
        if (tab.getParent() != this) {
            throw new IllegalArgumentException("Tab does not belong to this TabLayout.");
        } else {
            this.removeTabAt(tab.getPosition());
        }
    }

    public void removeTabAt(int position) {
        int selectedTabPosition = this.selectedTab != null ? this.selectedTab.getPosition() : 0;
        this.removeTabViewAt(position);
        Tab removedTab = this.tabs.remove(position);
        if (removedTab != null) {
            removedTab.reset();
            this.releaseFromTabPool(removedTab);
        }

        int newTabCount = this.tabs.size();

        for (int i = position; i < newTabCount; ++i) {
            ((Tab) this.tabs.get(i)).setPosition(i);
        }

        if (selectedTabPosition == position) {
            this.selectTab(this.tabs.isEmpty() ? null : this.tabs.get(Math.max(0, position - 1)));
        }

    }

    public void removeAllTabs() {
        for (int i = this.slidingTabIndicator.getChildCount() - 1; i >= 0; --i) {
            this.removeTabViewAt(i);
        }

        Iterator i = this.tabs.iterator();

        while (i.hasNext()) {
            Tab tab = (Tab) i.next();
            i.remove();
            tab.reset();
            this.releaseFromTabPool(tab);
        }

        this.selectedTab = null;
        selectedPosition = -1;
    }

    public void setTabMode(int mode) {
        if (mode != this.mode) {
            this.mode = mode;
            this.applyModeAndGravity();
        }

    }

    public int getTabMode() {
        return this.mode;
    }

    public void setTabGravity(int gravity) {
        if (this.tabGravity != gravity) {
            this.tabGravity = gravity;
            this.applyModeAndGravity();
        }
    }

    public int getTabGravity() {
        return this.tabGravity;
    }

    public void setSelectedTabIndicatorGravity(int indicatorGravity) {
        if (this.tabIndicatorGravity != indicatorGravity) {
            this.tabIndicatorGravity = indicatorGravity;
            ViewCompat.postInvalidateOnAnimation(this.slidingTabIndicator);
        }

    }

    public int getTabIndicatorGravity() {
        return this.tabIndicatorGravity;
    }

    public void setTabIndicatorFullWidth(boolean tabIndicatorFullWidth) {
        this.tabIndicatorFullWidth = tabIndicatorFullWidth;
        ViewCompat.postInvalidateOnAnimation(this.slidingTabIndicator);
    }

    public boolean isTabIndicatorFullWidth() {
        return this.tabIndicatorFullWidth;
    }

    public void setInlineLabel(boolean inline) {
        if (this.inlineLabel != inline) {
            this.inlineLabel = inline;

            for (int i = 0; i < this.slidingTabIndicator.getChildCount(); ++i) {
                View child = this.slidingTabIndicator.getChildAt(i);
                if (child instanceof TabChild) {
                    ((TabChild) child).updateOrientation(inlineLabel);
                }
            }
            this.applyModeAndGravity();
        }
    }

    public void setInlineLabelResource(@BoolRes int inlineResourceId) {
        this.setInlineLabel(this.getResources().getBoolean(inlineResourceId));
    }

    public boolean isInlineLabel() {
        return this.inlineLabel;
    }

    public void setUnboundedRipple(boolean unboundedRipple) {
        if (this.unboundedRipple != unboundedRipple) {
            this.unboundedRipple = unboundedRipple;

            for (int i = 0; i < this.slidingTabIndicator.getChildCount(); ++i) {
                View child = this.slidingTabIndicator.getChildAt(i);
                if (child instanceof TabChild) {
                    ((TabChild) child).updateBackgroundDrawable(this.getContext());
                }
            }
        }
    }

    public void setUnboundedRippleResource(@BoolRes int unboundedRippleResourceId) {
        this.setUnboundedRipple(this.getResources().getBoolean(unboundedRippleResourceId));
    }

    public boolean hasUnboundedRipple() {
        return this.unboundedRipple;
    }

    public void setTabTextColors(@Nullable ColorStateList textColor) {
        if (this.tabTextColors != textColor) {
            this.tabTextColors = textColor;
            this.updateAllTabs();
        }
    }

    @Nullable
    public ColorStateList getTabTextColors() {
        return this.tabTextColors;
    }

    public void setTabTextColors(int normalColor, int selectedColor) {
        this.setTabTextColors(ColorUtils.createColorStateList(normalColor, selectedColor));
    }

    public void setTabTextSize(float tabTextSize) {
        if (this.tabTextSize != tabTextSize) {
            this.tabTextSize = (int) tabTextSize;
            this.updateAllTabs();
        }
    }

    public void setTabIconTint(@Nullable ColorStateList iconTint) {
        if (this.tabIconTint != iconTint) {
            this.tabIconTint = iconTint;
            this.updateAllTabs();
        }

    }

    public void setTabIconTintResource(@ColorRes int iconTintResourceId) {
        this.setTabIconTint(AppCompatResources.getColorStateList(this.getContext(), iconTintResourceId));
    }

    @Nullable
    public ColorStateList getTabIconTint() {
        return this.tabIconTint;
    }

    @Nullable
    public ColorStateList getTabRippleColor() {
        return this.tabRippleColorStateList;
    }

    public void setTabRippleColor(@Nullable ColorStateList color) {
        if (this.tabRippleColorStateList != color) {
            this.tabRippleColorStateList = color;

            for (int i = 0; i < this.slidingTabIndicator.getChildCount(); ++i) {
                View child = this.slidingTabIndicator.getChildAt(i);
                Tab tab = tabs.get(i);
                if (tab != null && tab.getTabRippleColorStateList() == null) {
                    tab.setTabRippleColorStateList(tabRippleColorStateList);
                    if (child instanceof TabChild) {
                        ((TabChild) child).updateBackgroundDrawable(this.getContext());
                    }
                }
            }
        }
    }

    public void setTabRippleColorResource(@ColorRes int tabRippleColorResourceId) {
        this.setTabRippleColor(AppCompatResources.getColorStateList(this.getContext(), tabRippleColorResourceId));
    }

    @Nullable
    public Drawable getTabSelectedIndicator() {
        return this.tabSelectedIndicator;
    }

    public void setSelectedTabIndicator(@Nullable Drawable tabSelectedIndicator) {
        if (this.tabSelectedIndicator != tabSelectedIndicator) {
            this.tabSelectedIndicator = tabSelectedIndicator;
            ViewCompat.postInvalidateOnAnimation(this.slidingTabIndicator);
        }
    }

    public void setSelectedTabIndicator(@DrawableRes int tabSelectedIndicatorResourceId) {
        if (tabSelectedIndicatorResourceId != 0) {
            this.setSelectedTabIndicator(AppCompatResources.getDrawable(this.getContext(), tabSelectedIndicatorResourceId));
        } else {
            this.setSelectedTabIndicator(null);
        }
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        this.setupWithViewPager(viewPager, true);
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        this.setupWithViewPager(viewPager, autoRefresh, false);
    }

    private void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh, boolean implicitSetup) {
        if (this.viewPager != null) {
            if (this.pageChangeListener != null) {
                this.viewPager.removeOnPageChangeListener(this.pageChangeListener);
            }

            if (this.adapterChangeListener != null) {
                this.viewPager.removeOnAdapterChangeListener(this.adapterChangeListener);
            }
        }

        if (this.currentVpSelectedListener != null) {
            this.removeOnTabSelectedListener(this.currentVpSelectedListener);
            this.currentVpSelectedListener = null;
        }

        if (viewPager != null) {
            this.viewPager = viewPager;
            if (this.pageChangeListener == null) {
                this.pageChangeListener = new TabLayoutOnPageChangeListener(this);
            }

            this.pageChangeListener.reset();
            viewPager.addOnPageChangeListener(this.pageChangeListener);
            this.currentVpSelectedListener = new ViewPagerOnTabSelectedListener(viewPager);
            this.addOnTabSelectedListener(this.currentVpSelectedListener);
            PagerAdapter adapter = viewPager.getAdapter();
            if (adapter != null) {
                this.setPagerAdapter(adapter, autoRefresh);
            }

            if (this.adapterChangeListener == null) {
                this.adapterChangeListener = new AdapterChangeListener();
            }

            this.adapterChangeListener.setAutoRefresh(autoRefresh);
            viewPager.addOnAdapterChangeListener(this.adapterChangeListener);
            this.setScrollPosition(viewPager.getCurrentItem(), 0.0F, true);
        } else {
            this.viewPager = null;
            this.setPagerAdapter(null, false);
        }

        this.setupViewPagerImplicitly = implicitSetup;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setTabsFromPagerAdapter(@Nullable PagerAdapter adapter) {
        this.setPagerAdapter(adapter, false);
    }

    public boolean shouldDelayChildPressedState() {
        return this.getTabScrollRange() > 0;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.viewPager == null) {
            ViewParent vp = this.getParent();
            if (vp instanceof ViewPager) {
                this.setupWithViewPager((ViewPager) vp, true, true);
            }
        }

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.setupViewPagerImplicitly) {
            this.setupWithViewPager((ViewPager) null);
            this.setupViewPagerImplicitly = false;
        }

    }

    private int getTabScrollRange() {
        return Math.max(0, this.slidingTabIndicator.getWidth() - this.getWidth() - this.getPaddingLeft() - this.getPaddingRight());
    }

    void setPagerAdapter(@Nullable PagerAdapter adapter, boolean addObserver) {
        if (this.pagerAdapter != null && this.pagerAdapterObserver != null) {
            this.pagerAdapter.unregisterDataSetObserver(this.pagerAdapterObserver);
        }

        this.pagerAdapter = adapter;
        if (addObserver && adapter != null) {
            if (this.pagerAdapterObserver == null) {
                this.pagerAdapterObserver = new PagerAdapterObserver();
            }

            adapter.registerDataSetObserver(this.pagerAdapterObserver);
        }

        this.populateFromPagerAdapter();
    }

    void populateFromPagerAdapter() {
        this.removeAllTabs();
        if (this.pagerAdapter != null) {
            int adapterCount = this.pagerAdapter.getCount();

            int curItem;
            for (curItem = 0; curItem < adapterCount; ++curItem) {
                if (pagerAdapter instanceof TabAdapter) {
                    this.addTab(((TabAdapter) pagerAdapter).getTab(curItem), false);
                } else {
                    this.addTab(this.newTab().setText(this.pagerAdapter.getPageTitle(curItem)), false);
                }
            }

            if (this.viewPager != null && adapterCount > 0) {
                curItem = this.viewPager.getCurrentItem();
                if (curItem != this.getSelectedTabPosition() && curItem < this.getTabCount()) {
                    this.selectTab(this.getTabAt(curItem));
                }
            }
        }
    }

    private void updateAllTabs() {
        int i = 0;

        for (int z = this.tabs.size(); i < z; ++i) {
            if (tabs.get(i).getTextColor() == null) {
                tabs.get(i).setTextColor(this.tabTextColors);
            }

            if (tabs.get(i).getTextColor() == null) {
                tabs.get(i).setTabIconTint(this.tabIconTint);
            }

            if (tabs.get(i).getTabTextSize() == 0) {
                tabs.get(i).setTabTextSize(this.tabTextSize);
            }
        }

    }

    private TabView createTabView(@NonNull Tab tab) {
        TabChild tabItem = this.tabViewPool != null ? this.tabViewPool.acquire() : null;
        TabView tabView = (tabItem instanceof TabView) ? (TabView) tabItem : new TabView(this.getContext());
        tabView.setTab(tab);
        tabView.setFocusable(true);
        tabView.setMinimumWidth(this.getTabMinWidth());
        if (TextUtils.isEmpty(tab.getContentDesc())) {
            tabView.setContentDescription(tab.getText());
        } else {
            tabView.setContentDescription(tab.getContentDesc());
        }

        return tabView;
    }

    private void configureTab(Tab tab, int position) {
        tab.setPosition(position);

        if (tab.getTextColor() == null) {
            tab.setTextColor(this.tabTextColors);
        }

        if (tab.getTabIconTint() == null) {
            tab.setTabIconTint(this.tabIconTint);
        }

        if (tab.getTabIconTintMode() == null) {
            tab.setTabIconTintMode(this.tabIconTintMode);
        }

        if (tab.getTabTextSize() == 0) {
            tab.setTabTextSize(tabTextSize);
        }

        if (tab.getTabBackgroundResId() == 0) {
            tab.setTabBackgroundResId(tabBackgroundResId);
        }

        if (tab.getTabRippleColorStateList() == null) {
            tab.setTabRippleColorStateList(tabRippleColorStateList);
        }

        tab.setInlineLabel(inlineLabel);

        tab.setTabTextBold(tabTextBold);

        tab.setTabPadding(tab.getTabPaddingStart() > 0 ? tab.getTabPaddingStart() : tabPaddingStart,
                tab.getTabPaddingTop() > 0 ? tab.getTabPaddingStart() : tabPaddingTop,
                tab.getTabPaddingEnd() > 0 ? tab.getTabPaddingStart() : tabPaddingEnd,
                tab.getTabPaddingBottom() > 0 ? tab.getTabPaddingStart() : tabPaddingBottom);

        this.tabs.add(position, tab);
        int count = this.tabs.size();

        for (int i = position + 1; i < count; ++i) {
            this.tabs.get(i).setPosition(i);
        }
    }

    private void addTabView(Tab tab) {
        View tabView = tab.getTabItem().getView();
        this.slidingTabIndicator.addView(tabView, tab.getPosition(), this.createLayoutParamsForTabs(tab.getPosition()));
    }

    public void addView(View child) {
        this.addViewInternal(child);
    }

    public void addView(View child, int index) {
        this.addViewInternal(child);
    }

    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        this.addViewInternal(child);
    }

    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        this.addViewInternal(child);
    }

    private void addViewInternal(View child) {
        if (child instanceof TabItem) {
            this.addTabFromItemView((TabItem) child);
        } else {
            throw new IllegalArgumentException("Only TabChild instances can be added to TabLayout");
        }
    }

    private LinearLayout.LayoutParams createLayoutParamsForTabs(int position) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -1);
        this.updateTabViewLayoutParams(lp, position > 0 ? tabDividerWidth : 0);
        return lp;
    }

    private void updateTabViewLayoutParams(LinearLayout.LayoutParams lp, int dividerWidth) {
        lp.leftMargin = dividerWidth;
        if (this.mode == 1 && this.tabGravity == 0) {
            lp.width = 0;
            lp.weight = 1.0F;
        } else {
            lp.width = -2;
            lp.weight = 0.0F;
        }
    }

    @Dimension(
            unit = 1
    )
    int dpToPx(@Dimension(unit = 0) int dps) {
        return Math.round(this.getResources().getDisplayMetrics().density * (float) dps);
    }

    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < this.slidingTabIndicator.getChildCount(); ++i) {
            View tabView = this.slidingTabIndicator.getChildAt(i);
            if (tabView instanceof TabChild) {
                ((TabChild) tabView).drawBackground(canvas);
            }
        }

        super.onDraw(canvas);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int idealHeight = this.dpToPx(this.getDefaultHeight()) + this.getPaddingTop() + this.getPaddingBottom();
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case -2147483648:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(idealHeight, MeasureSpec.getSize(heightMeasureSpec)), MeasureSpec.EXACTLY);
                break;
            case 0:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(idealHeight, MeasureSpec.EXACTLY);
        }

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED) {
            this.tabMaxWidth = this.requestedTabMaxWidth > 0 ? this.requestedTabMaxWidth : specWidth - this.dpToPx(56);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.getChildCount() == 1) {
            View child = this.getChildAt(0);
            boolean remeasure = false;
            switch (this.mode) {
                case 0:
                    remeasure = child.getMeasuredWidth() < this.getMeasuredWidth();
                    break;
                case 1:
                    remeasure = child.getMeasuredWidth() != this.getMeasuredWidth();
            }

            if (remeasure) {
                int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, this.getPaddingTop() + this.getPaddingBottom(), child.getLayoutParams().height);
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }

    }

    private void removeTabViewAt(int position) {
        View view = this.slidingTabIndicator.getChildAt(position);
        this.slidingTabIndicator.removeViewAt(position);
        if (view != null && view instanceof TabChild) {
            ((TabChild) view).reset();
            this.tabViewPool.release((TabChild) view);
        }

        this.requestLayout();
    }

    private void animateToTab(int newPosition) {
        if (newPosition != -1) {
            if (this.getWindowToken() != null && ViewCompat.isLaidOut(this) && !this.slidingTabIndicator.childrenNeedLayout()) {
                int startScrollX = this.getScrollX();
                int targetScrollX = this.calculateScrollXForTab(newPosition, 0.0F);
                if (startScrollX != targetScrollX) {
                    this.ensureScrollAnimator();
                    this.scrollAnimator.setIntValues(new int[]{startScrollX, targetScrollX});
                    this.scrollAnimator.start();
                }

                this.slidingTabIndicator.animateIndicatorToPosition(newPosition, this.tabIndicatorAnimationDuration);
            } else {
                this.setScrollPosition(newPosition, 0.0F, true);
            }
        }
    }

    private void ensureScrollAnimator() {
        if (this.scrollAnimator == null) {
            this.scrollAnimator = new ValueAnimator();
            this.scrollAnimator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            this.scrollAnimator.setDuration((long) this.tabIndicatorAnimationDuration);
            this.scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animator) {
                    JTabLayout.this.scrollTo((Integer) animator.getAnimatedValue(), 0);
                }
            });
        }

    }

    public void setScrollAnimatorListener(Animator.AnimatorListener listener) {
        this.ensureScrollAnimator();
        this.scrollAnimator.addListener(listener);
    }

    private void setSelectedTabView(int position) {
        setSelectedTabView(position, false);
    }

    private void setSelectedTabView(int position, boolean isViewpagerScroll) {

        if (selectedPosition == position) {
            return;
        }

        final View selectedChild = this.slidingTabIndicator.getChildAt(selectedPosition);
        final View newChild = this.slidingTabIndicator.getChildAt(position);

        selectedPosition = position;

        if (isViewpagerScroll) {
            updateTab(selectedChild, newChild, false, tabColorTransitionScroll);
            return;
        }

        final boolean isScale = tabScaleTransitionScroll > 1f;

        if (!isScale && !tabColorTransitionScroll) {
            updateTab(selectedChild, newChild, false, false);
            return;
        }

        if (scaleAnimator != null && scaleAnimator.isRunning()) {
            scaleAnimator.cancel();
        }

        PropertyValuesHolder selectedScaleValues = PropertyValuesHolder.ofFloat("selectedScale", tabScaleTransitionScroll, 1.0f);
        PropertyValuesHolder newScaleValues = PropertyValuesHolder.ofFloat("newScale", 1.0f, tabScaleTransitionScroll);
        PropertyValuesHolder selectedColorValues = PropertyValuesHolder.ofFloat("selectedColor", 1.0f, .0f);
        PropertyValuesHolder newColorValues = PropertyValuesHolder.ofFloat("newColor", .0f, 1.0f);

        ValueAnimator animator = scaleAnimator = ValueAnimator.ofPropertyValuesHolder(selectedScaleValues, newScaleValues, selectedColorValues, newColorValues);
        animator.setDuration(200);
        animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float selectedScale = (float) animation.getAnimatedValue("selectedScale");
                float newScale = (float) animation.getAnimatedValue("newScale");
                float selectedColor = (float) animation.getAnimatedValue("selectedColor");
                float newColor = (float) animation.getAnimatedValue("newColor");

                if (selectedChild instanceof TabChild) {
//                    selectedChild.setPivotX(100);
//                    selectedChild.setPivotY(selectedChild.getBottom());
                    if (isScale) {
                        ((TabChild) selectedChild).updateScale(selectedScale);
                    }

                    if (tabColorTransitionScroll) {
                        ((TabChild) selectedChild).updateColor(selectedColor);
                    }

                }

                if (newChild instanceof TabChild) {
//                    child.setPivotX(100);
//                    child.setPivotY(child.getBottom());
                    if (isScale) {
                        ((TabChild) newChild).updateScale(newScale);
                    }

                    if (tabColorTransitionScroll) {
                        ((TabChild) newChild).updateColor(newColor);
                    }

                }

            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                updateTab(selectedChild, newChild, isScale, tabColorTransitionScroll);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                updateTab(selectedChild, newChild, isScale, tabColorTransitionScroll);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
//        int tabCount = this.slidingTabIndicator.getChildCount();
//        if (position < tabCount) {
//            for (int i = 0; i < tabCount; ++i) {
//                View child = this.slidingTabIndicator.getChildAt(i);
//                child.setSelected(i == position);
//                child.setActivated(i == position);
////                if (i == position) {
////                    child.setScaleX(1.5f);
////                    child.setScaleY(1.5f);
////                } else {
////                    child.setScaleX(1.0f);
////                    child.setScaleY(1.0f);
////                }
//            }
//        }

    }

    private void updateTab(View selectedChild, View newChild, boolean isScale, boolean isColorTransitionScroll) {

        if (selectedChild != null) {
            selectedChild.setSelected(false);
            selectedChild.setActivated(false);
        }

        if (newChild != null) {
            newChild.setSelected(true);
            newChild.setActivated(true);
        }


        if (selectedChild instanceof TabChild) {
            if (isScale) {
                ((TabChild) selectedChild).updateScale(1.0f);
            }

            if (isColorTransitionScroll) {
                ((TabChild) selectedChild).updateColor(.0f);
            }
        }

        if (newChild instanceof TabChild) {
            if (isScale) {
                ((TabChild) newChild).updateScale(tabScaleTransitionScroll);
            }

            if (isColorTransitionScroll) {
                ((TabChild) newChild).updateColor(1.0f);
            }
        }
    }

    public void selectTab(int position) {
        this.selectTab(position, true);
    }

    public void selectTab(int position, boolean isCallback) {
        if (position < tabs.size()) {
            this.selectTab(tabs.get(position), isCallback);
        }
    }

    public void selectTab(int position, boolean updateIndicator, boolean isCallback) {
        if (position < tabs.size()) {
            this.selectTab(tabs.get(position), updateIndicator, isCallback);
        }
    }

    public void selectTab(Tab tab) {
        this.selectTab(tab, true);
    }

    public void selectTab(Tab tab, boolean isCallback) {
        this.selectTab(tab, true, isCallback);
    }

    public void selectTab(Tab tab, boolean updateIndicator, boolean isCallback) {
        Tab currentTab = this.selectedTab;
        if (currentTab == tab) {
            if (currentTab != null) {
                this.dispatchTabReselected(tab);
                this.animateToTab(tab.getPosition());
            }
        } else {
            int newPosition = tab != null ? tab.getPosition() : -1;
            if (updateIndicator) {
                if ((currentTab == null || currentTab.getPosition() == -1) && newPosition != -1) {
                    this.setScrollPosition(newPosition, 0.0F, true);
                } else {
                    this.animateToTab(newPosition);
                }

                if (newPosition != -1) {
                    this.setSelectedTabView(newPosition);
                }
            }

            this.selectedTab = tab;
            if (currentTab != null && isCallback) {
                this.dispatchTabUnselected(currentTab);
            }

            if (tab != null && isCallback) {
                this.dispatchTabSelected(tab);
            }
        }
    }

    private void dispatchTabSelected(@NonNull Tab tab) {
        for (int i = this.selectedListeners.size() - 1; i >= 0; --i) {
            ((BaseOnTabSelectedListener) this.selectedListeners.get(i)).onTabSelected(tab);
        }

    }

    private void dispatchTabUnselected(@NonNull Tab tab) {
        for (int i = this.selectedListeners.size() - 1; i >= 0; --i) {
            ((BaseOnTabSelectedListener) this.selectedListeners.get(i)).onTabUnselected(tab);
        }

    }

    private void dispatchTabReselected(@NonNull Tab tab) {
        for (int i = this.selectedListeners.size() - 1; i >= 0; --i) {
            ((BaseOnTabSelectedListener) this.selectedListeners.get(i)).onTabReselected(tab);
        }

    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        if (this.mode == 0) {
            View selectedChild = this.slidingTabIndicator.getChildAt(position);
            View nextChild = position + 1 < this.slidingTabIndicator.getChildCount() ? this.slidingTabIndicator.getChildAt(position + 1) : null;
            int selectedWidth = selectedChild != null ? selectedChild.getWidth() + tabDividerWidth : 0;
            int nextWidth = nextChild != null ? nextChild.getWidth() + tabDividerWidth : 0;
            int scrollBase = selectedChild.getLeft() + selectedWidth / 2 - this.getWidth() / 2;
            int scrollOffset = (int) ((float) (selectedWidth + nextWidth) * 0.5F * positionOffset);
            return ViewCompat.getLayoutDirection(this) == 0 ? scrollBase + scrollOffset : scrollBase - scrollOffset;
        } else {
            return 0;
        }
    }

    private void applyModeAndGravity() {
        int paddingStart = 0;
        if (this.mode == 0) {
            paddingStart = Math.max(0, this.contentInsetStart - this.tabPaddingStart);
        }

        ViewCompat.setPaddingRelative(this.slidingTabIndicator, paddingStart, 0, 0, 0);
        switch (this.mode) {
            case 0:
                this.slidingTabIndicator.setGravity(8388611);
                break;
            case 1:
                this.slidingTabIndicator.setGravity(1);
        }

        this.updateTabViews(true);
    }

    void updateTabViews(boolean requestLayout) {
        for (int i = 0; i < this.slidingTabIndicator.getChildCount(); ++i) {
            View child = this.slidingTabIndicator.getChildAt(i);
            child.setMinimumWidth(this.getTabMinWidth());
            this.updateTabViewLayoutParams((LinearLayout.LayoutParams) child.getLayoutParams(), i > 0 ? tabDividerWidth : 0);
            if (requestLayout) {
                child.requestLayout();
            }
        }

    }

    @Dimension(
            unit = 0
    )
    private int getDefaultHeight() {
        boolean hasIconAndText = false;
        int i = 0;

        for (int count = this.tabs.size(); i < count; ++i) {
            Tab tab = this.tabs.get(i);
            if (tab != null && tab.getNormalIcon() != null && !TextUtils.isEmpty(tab.getText())) {
                hasIconAndText = true;
                break;
            }
        }

        return hasIconAndText && !this.inlineLabel ? 72 : 48;
    }

    private int getTabMinWidth() {
        if (this.requestedTabMinWidth != -1) {
            return this.requestedTabMinWidth;
        } else {
            return this.mode == 0 ? this.scrollableTabMinWidth : 0;
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return this.generateDefaultLayoutParams();
    }

    int getTabMaxWidth() {
        return this.tabMaxWidth;
    }

    private class AdapterChangeListener implements ViewPager.OnAdapterChangeListener {
        private boolean autoRefresh;

        AdapterChangeListener() {
        }

        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
            if (JTabLayout.this.viewPager == viewPager) {
                JTabLayout.this.setPagerAdapter(newAdapter, this.autoRefresh);
            }

        }

        void setAutoRefresh(boolean autoRefresh) {
            this.autoRefresh = autoRefresh;
        }
    }

    private class PagerAdapterObserver extends DataSetObserver {
        PagerAdapterObserver() {
        }

        public void onChanged() {
            populateFromPagerAdapter();
        }

        public void onInvalidated() {
            populateFromPagerAdapter();
        }
    }

    public static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
        private final ViewPager viewPager;

        public ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        public void onTabSelected(Tab tab) {
            this.viewPager.setCurrentItem(tab.getPosition());
        }

        public void onTabUnselected(Tab tab) {
        }

        public void onTabReselected(Tab tab) {
        }
    }

    public static class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private final WeakReference<JTabLayout> tabLayoutRef;
        private int previousScrollState;
        private int scrollState;

        public TabLayoutOnPageChangeListener(JTabLayout tabLayout) {
            this.tabLayoutRef = new WeakReference(tabLayout);
        }

        public void onPageScrollStateChanged(int state) {
            this.previousScrollState = this.scrollState;
            this.scrollState = state;
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            JTabLayout tabLayout = this.tabLayoutRef.get();
            if (tabLayout != null) {
                boolean updateText = this.scrollState != 2 || this.previousScrollState == 1;
                boolean updateIndicator = this.scrollState != 2 || this.previousScrollState != 0;
                tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator);
            }
        }

        public void onPageSelected(int position) {
            JTabLayout tabLayout = this.tabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position && position < tabLayout.getTabCount()) {
                boolean updateIndicator = this.scrollState == 0 || this.scrollState == 2 && this.previousScrollState == 0;
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator, true);
            }
        }

        void reset() {
            this.previousScrollState = this.scrollState = 0;
        }
    }

    private class SlidingTabIndicator extends LinearLayout {
        private int selectedIndicatorHeight;
        private final Paint selectedIndicatorPaint;
        private final GradientDrawable defaultSelectionIndicator;
        int selectedPosition = -1;
        float selectionOffset;
        private int layoutDirection = -1;
        private IndicatorPoint indicatorPoint = new IndicatorPoint();
        private ValueAnimator indicatorAnimator;

        private Paint dividerPaint;

        SlidingTabIndicator(Context context) {
            super(context);
            this.setWillNotDraw(false);
            this.selectedIndicatorPaint = new Paint();
            this.defaultSelectionIndicator = new GradientDrawable();
            dividerPaint = new Paint();
        }

        void setSelectedIndicatorColor(int color) {
            if (this.selectedIndicatorPaint.getColor() != color) {
                this.selectedIndicatorPaint.setColor(color);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        void setSelectedIndicatorHeight(int height) {
            if (this.selectedIndicatorHeight != height) {
                this.selectedIndicatorHeight = height;
                ViewCompat.postInvalidateOnAnimation(this);
            }

        }

        boolean childrenNeedLayout() {
            int i = 0;

            for (int z = this.getChildCount(); i < z; ++i) {
                View child = this.getChildAt(i);
                if (child.getWidth() <= 0) {
                    return true;
                }
            }

            return false;
        }

        void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
            if (this.indicatorAnimator != null && this.indicatorAnimator.isRunning()) {
                this.indicatorAnimator.cancel();
            }

            this.selectedPosition = position;
            this.selectionOffset = positionOffset;
            this.updateIndicatorPosition();
        }

        float getIndicatorPosition() {
            return (float) this.selectedPosition + this.selectionOffset;
        }

        public void onRtlPropertiesChanged(int layoutDirection) {
            super.onRtlPropertiesChanged(layoutDirection);
            if (Build.VERSION.SDK_INT < 23 && this.layoutDirection != layoutDirection) {
                this.requestLayout();
                this.layoutDirection = layoutDirection;
            }

        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
                if (JTabLayout.this.mode == 1 && JTabLayout.this.tabGravity == 1) {
                    int count = this.getChildCount();
                    int largestTabWidth = 0;
                    int gutter = 0;

                    for (int z = count; gutter < z; ++gutter) {
                        View child = this.getChildAt(gutter);
                        if (child.getVisibility() == VISIBLE) {
                            largestTabWidth = Math.max(largestTabWidth, child.getMeasuredWidth());
                        }
                    }

                    if (largestTabWidth <= 0) {
                        return;
                    }

                    gutter = dpToPx(16);
                    boolean remeasure = false;
                    if (largestTabWidth * count > this.getMeasuredWidth() - gutter * 2) {
                        JTabLayout.this.tabGravity = 0;
                        JTabLayout.this.updateTabViews(false);
                        remeasure = true;
                    } else {
                        for (int i = 0; i < count; ++i) {
                            android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) this.getChildAt(i).getLayoutParams();
                            if (lp.width != largestTabWidth || lp.weight != 0.0F) {
                                lp.width = largestTabWidth;
                                lp.weight = 0.0F;
                                remeasure = true;
                            }
                        }
                    }

                    if (remeasure) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }
                }

            }
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.indicatorAnimator != null && this.indicatorAnimator.isRunning()) {
                this.indicatorAnimator.cancel();
                long duration = this.indicatorAnimator.getDuration();
                this.animateIndicatorToPosition(this.selectedPosition, Math.round((1.0F - this.indicatorAnimator.getAnimatedFraction()) * (float) duration));
            } else {
                this.updateIndicatorPosition();
            }
        }

        private void updateIndicatorPosition() {
            View selectedTitle = this.getChildAt(this.selectedPosition);
            int left;
            int right;
            if (selectedTitle != null && selectedTitle.getWidth() > 0) {
                left = selectedTitle.getLeft();
                right = selectedTitle.getRight();
                if (!JTabLayout.this.tabIndicatorFullWidth && selectedTitle instanceof TabChild) {
                    this.calculateTabViewContentBounds((TabChild) selectedTitle, JTabLayout.this.tabViewContentBounds);
                    left = (int) JTabLayout.this.tabViewContentBounds.left;
                    right = (int) JTabLayout.this.tabViewContentBounds.right;
                }

                if (this.selectionOffset > 0.0F && this.selectedPosition < this.getChildCount() - 1) {
                    View nextTitle = this.getChildAt(this.selectedPosition + 1);
                    int nextTitleLeft = nextTitle.getLeft();
                    int nextTitleRight = nextTitle.getRight();
                    if (!JTabLayout.this.tabIndicatorFullWidth && nextTitle instanceof TabChild) {
                        this.calculateTabViewContentBounds((TabChild) nextTitle, JTabLayout.this.tabViewContentBounds);
                        nextTitleLeft = (int) JTabLayout.this.tabViewContentBounds.left;
                        nextTitleRight = (int) JTabLayout.this.tabViewContentBounds.right;
                    }

                    if (tabIndicatorTransitionScroll) {
                        float offR = selectionOffset * 2 - 1;
                        float offL = selectionOffset * 2;

                        if (selectedPosition + 1 < selectedPosition && selectionOffset > 0) {
                            if (offR < 0) {
                                offR = 0;
                            }
                            if (1 - offL < 0) {
                                offL = 1;
                            }
                        } else {
                            offL = selectionOffset * 2 - 1;
                            offR = selectionOffset * 2;
                            if (offL < 0) {
                                offL = 0;
                            }
                            if (1 - offR < 0) {
                                offR = 1;
                            }
                        }
                        left += ((nextTitleLeft - left) * offL);
                        right += ((nextTitleRight - right) * offR);
                    } else {
                        left = (int) (this.selectionOffset * (float) nextTitleLeft + (1.0F - this.selectionOffset) * (float) left);
                        right = (int) (this.selectionOffset * (float) nextTitleRight + (1.0F - this.selectionOffset) * (float) right);
                    }
                }
            } else {
                right = -1;
                left = -1;
            }

            this.setIndicatorPosition(left, right);
        }

        void setIndicatorPosition(float left, float right) {
            if (left != indicatorPoint.left || right != indicatorPoint.right) {
                indicatorPoint.left = left;
                indicatorPoint.right = right;
                ViewCompat.postInvalidateOnAnimation(this);
            }

        }

        void animateIndicatorToPosition(final int position, int duration) {
            if (this.indicatorAnimator != null && this.indicatorAnimator.isRunning()) {
                this.indicatorAnimator.cancel();
            }

            View targetView = this.getChildAt(position);
            if (targetView == null) {
                this.updateIndicatorPosition();
            } else {
                IndicatorPoint target = new IndicatorPoint();
                target.left = targetView.getLeft();
                target.right = targetView.getRight();

                if (!JTabLayout.this.tabIndicatorFullWidth && targetView instanceof TabChild) {
                    this.calculateTabViewContentBounds((TabChild) targetView, JTabLayout.this.tabViewContentBounds);
                    target.left = (int) JTabLayout.this.tabViewContentBounds.left;
                    target.right = (int) JTabLayout.this.tabViewContentBounds.right;
                }

                if (!indicatorPoint.equals(target)) {
                    ValueAnimator animator = this.indicatorAnimator = ValueAnimator.ofObject(tabIndicatorTransitionScroll ? new TransitionIndicatorEvaluator() : new DefIndicatorEvaluator(), indicatorPoint, target);
                    animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
                    animator.setDuration((long) duration);
//                    animator.setFloatValues(new float[]{0.0F, 1.0F});
//                    final int finalTargetLeft = targetLeft;
//                    final int finalTargetRight = targetRight;
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animator) {
                            IndicatorPoint p = (IndicatorPoint) animator.getAnimatedValue();
                            SlidingTabIndicator.this.setIndicatorPosition(p.left, p.right);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            SlidingTabIndicator.this.selectedPosition = position;
                            SlidingTabIndicator.this.selectionOffset = 0.0F;
                        }
                    });
                    animator.start();
                }
            }
        }

        private void calculateTabViewContentBounds(TabChild tabView, RectF contentBounds) {
            if (tabIndicatorWidth > 0 || tabIndicatorWidthScale > 0) {
                int left = tabView.getView().getLeft();
                int right = tabView.getView().getRight();
                int tabWidth = right - left;

                tabIndicatorWidth = Math.min(tabWidth, tabIndicatorWidth > 0 ? tabIndicatorWidth : (int) (tabWidth * tabIndicatorWidthScale));
                int tabIndicatorInt = Math.max(0, tabWidth - tabIndicatorWidth) / 2;

                int contentLeftBounds = left + tabIndicatorInt;
                int contentRightBounds = contentLeftBounds + tabIndicatorWidth;
                contentBounds.set((float) contentLeftBounds, 0.0F, (float) contentRightBounds, 0.0F);
            } else {
                int tabViewContentWidth = tabView.getContentWidth();
                if (tabViewContentWidth < dpToPx(24)) {
                    tabViewContentWidth = dpToPx(24);
                }

                int tabViewCenter = (tabView.getView().getLeft() + tabView.getView().getRight()) / 2;
                int contentLeftBounds = tabViewCenter - tabViewContentWidth / 2;
                int contentRightBounds = tabViewCenter + tabViewContentWidth / 2;
                contentBounds.set((float) contentLeftBounds, 0.0F, (float) contentRightBounds, 0.0F);
            }
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            drawDivider(canvas);
            drawIndicator(canvas);
        }

        private void drawDivider(Canvas canvas) {
            if (tabDividerWidth > 0 && getChildCount() > 0) {
                dividerPaint.setStrokeWidth(tabDividerWidth);
                dividerPaint.setColor(tabDividerColor);
                tabDividerHeight = tabDividerHeight > -1 ? tabDividerHeight : getHeight();
                for (int i = 0; i < getChildCount() - 1; i++) {
                    View tab = getChildAt(i);
                    canvas.drawRect(tab.getRight(), (getHeight() - tabDividerHeight) / 2, tab.getRight() + tabDividerWidth, (getHeight() - tabDividerHeight) / 2 + tabDividerHeight, dividerPaint);
                }
            }
        }

        private void drawIndicator(Canvas canvas) {
            int indicatorHeight = 0;
            if (JTabLayout.this.tabSelectedIndicator != null) {
                indicatorHeight = JTabLayout.this.tabSelectedIndicator.getIntrinsicHeight();
            }

            if (this.selectedIndicatorHeight >= 0) {
                indicatorHeight = this.selectedIndicatorHeight;
            }

            int indicatorTop = 0;
            int indicatorBottom = 0;
            switch (JTabLayout.this.tabIndicatorGravity) {
                case 0:
                    indicatorTop = this.getHeight() - indicatorHeight - tabIndicatorMargin;
                    indicatorBottom = this.getHeight() - tabIndicatorMargin;
                    break;
                case 1:
                    indicatorTop = (this.getHeight() - indicatorHeight) / 2;
                    indicatorBottom = (this.getHeight() + indicatorHeight) / 2;
                    break;
                case 2:
                    indicatorTop = tabIndicatorMargin;
                    indicatorBottom = indicatorHeight;
                    break;
                case 3:
                    indicatorTop = tabIndicatorMargin;
                    indicatorBottom = this.getHeight() - tabIndicatorMargin;
            }

            if (indicatorPoint.left >= 0 && indicatorPoint.right > indicatorPoint.left) {
                Drawable selectedIndicator = DrawableCompat.wrap(JTabLayout.this.tabSelectedIndicator != null ? JTabLayout.this.tabSelectedIndicator : this.defaultSelectionIndicator);
                selectedIndicator.setBounds((int) indicatorPoint.left, indicatorTop, (int) indicatorPoint.right, indicatorBottom);
                if (this.selectedIndicatorPaint != null) {
                    if (Build.VERSION.SDK_INT == 21) {
                        selectedIndicator.setColorFilter(this.selectedIndicatorPaint.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {
                        DrawableCompat.setTint(selectedIndicator, this.selectedIndicatorPaint.getColor());
                    }
                }

                selectedIndicator.draw(canvas);
            }
        }
    }

    public interface OnTabSelectedListener extends BaseOnTabSelectedListener<Tab> {
    }

    public interface BaseOnTabSelectedListener<T extends Tab> {
        void onTabSelected(T var1);

        void onTabUnselected(T var1);

        void onTabReselected(T var1);
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public @interface TabIndicatorGravity {
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public @interface TabGravity {
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public @interface Mode {
    }


    private float lerp(float startValue, float endValue, float fraction) {
        return startValue + fraction * (endValue - startValue);
    }

    private int lerp(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (float) (endValue - startValue));
    }
}
