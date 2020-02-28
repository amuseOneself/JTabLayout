package com.liang.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.util.Pools
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.liang.tablayout3x.ItemDecoration
import com.liang.tablayout3x.R
import com.liang.tablayout3x.Tab
import com.liang.utils.createColorStateList
import com.liang.utils.getColorStateList
import com.liang.utils.getDrawable
import com.liang.utils.parseTintMode


/**
 * TODO: document your custom view class.
 */
class TabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    companion object {
        const val Horizontal = 0
        const val Vertical = 1
        const val ModeScrollable = 0
        const val ModeFixed = 1
        const val IndicatorTierBackground = 0
        const val IndicatorTierForeground = 1
        const val IndicatorGravityStart = 0
        const val IndicatorGravityEnd = 1
        const val IndicatorGravityCenter = 2
    }

    var tabUseClipToOutline: Boolean = false
    private val tabs = arrayListOf<Tab>()
    private val tabPool: Pools.Pool<Tab> = Pools.SynchronizedPool(16)
    private val selectedListeners by lazy { arrayListOf<BaseOnTabSelectedListener>() }
    private var selectedTab: Tab? = null

    var layoutManager: LayoutManager? = null
        set(value) {
            if (value != field) {
                field = value
                initTabLayoutManager(field)
            }
        }

    var tabIndicatorGravity = 0
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }

    var tabIndicator: Drawable? = null
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }

    var tabIndicatorFullWidth = true
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }

    var tabIndicatorFullHeight = true
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }

    var tabAnimationDuration = 300L

    var tabIndicatorTier = 0
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }

    var tabIndicatorWidth = 0
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }

    var tabIndicatorHeight = 10
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }

    var tabIndicatorMargin = 0
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }

    var tabIndicatorWidthScale = 0.5f
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }

    var tabIndicatorTransitionScroll = false

    var tabIndicatorColor = Color.BLUE
        set(value) {
            if (field != value) {
                field = value
                layoutManager?.postInvalidateOnAnimation()
            }
        }


    private var contentInsetStart = 0

    var tabScaleTransitionScroll = 0f
    var tabColorTransitionScroll = false
    var unboundedRipple = false
        set(value) {
            if (field != value) {
                field = value
                tabs.forEach {
                    it.tabUnboundedRipple = field
                }
            }
        }

    var tabTextColors: ColorStateList? = null
        set(value) {
            if (field != value) {
                field = value
                tabs.forEach {
                    it.tabTitleColors = field
                }
            }
        }

    private var tabIconTintMode: PorterDuff.Mode? = null

    var tabIconTint: ColorStateList? = null
        set(value) {
            if (field != value) {
                field = value
                tabs.forEach {
                    it.tabIconTint = field
                }
            }
        }
    var tabRippleColor: Int = 0
        set(value) {
            if (field != value) {
                field = value
                tabs.forEach {
                    it.tabRippleColor = field
                }
            }
        }

    var tabTextSize = 0
        set(value) {
            if (field != value) {
                field = value
                tabs.forEach {
                    it.tabTitleSize = field.toFloat()
                }
            }
        }

    private var tabBackgroundResId = 0
    private var tabPaddingStart = 0
    private var tabPaddingTop = 0
    private var tabPaddingEnd = 0
    private var tabPaddingBottom = 0
    private var tabTextBold = false

    var inlineLabel: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                tabs.forEach {
                    it.inlineLabel = field
                }
            }
        }

    private var tabDividerWidth = 0
    private var tabDividerHeight = 0
    private var tabDividerColor = 0

    private var itemDecoration: ItemDecoration? = null

    fun addOnTabSelectedListener(listener: OnTabSelectedListener) {
        if (!selectedListeners.contains(listener)) {
            selectedListeners.add(listener)
        }
    }

    fun removeOnTabSelectedListener(listener: OnTabSelectedListener) {
        selectedListeners.remove(listener)
    }

    fun clearOnTabSelectedListeners() {
        selectedListeners.clear()
    }


    init {
        initAttrs(attrs, defStyleAttr)
    }

    @SuppressLint("NewApi")
    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyleAttr, 0)

        this.tabIndicatorHeight =
            typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabIndicatorHeight, -1)
        this.tabIndicatorColor = typedArray.getColor(R.styleable.TabLayout_tabIndicatorColor, 0)
        this.tabIndicator = context.getDrawable(typedArray, R.styleable.TabLayout_tabIndicator)
        this.tabIndicatorGravity = typedArray.getInt(R.styleable.TabLayout_tabIndicatorGravity, 1)
        this.tabIndicatorTier = typedArray.getInt(R.styleable.TabLayout_tabIndicatorTier, 0)
        this.tabIndicatorWidth =
            typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabIndicatorWidth, 0)
        this.tabIndicatorMargin =
            typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabIndicatorMargin, 0)
        this.tabIndicatorWidthScale =
            typedArray.getFloat(R.styleable.TabLayout_tabIndicatorWidthScale, 0f)
        this.tabIndicatorFullWidth =
            typedArray.getBoolean(R.styleable.TabLayout_tabIndicatorFullWidth, false)
        this.tabIndicatorFullHeight =
            typedArray.getBoolean(R.styleable.TabLayout_tabIndicatorFullHeight, false)
        this.tabIndicatorTransitionScroll =
            typedArray.getBoolean(R.styleable.TabLayout_tabIndicatorTransitionScroll, false)

        this.clipToOutline = typedArray.getBoolean(R.styleable.TabLayout_useClipToOutline, false)
        this.tabUseClipToOutline =
            typedArray.getBoolean(R.styleable.TabLayout_tabUseClipToOutline, false)
        this.tabAnimationDuration =
            typedArray.getInt(R.styleable.TabLayout_tabAnimationDuration, 300).toLong()
        this.tabPaddingStart = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabPadding, 0)
            .also { tabPaddingBottom = it }.also { tabPaddingEnd = it }
            .also { tabPaddingTop = it }
        this.tabPaddingStart =
            typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingStart, tabPaddingStart)
        this.tabPaddingTop =
            typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingTop, tabPaddingTop)
        this.tabPaddingEnd =
            typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingEnd, tabPaddingEnd)
        this.tabPaddingBottom = typedArray.getDimensionPixelSize(
            R.styleable.TabLayout_tabPaddingBottom,
            tabPaddingBottom
        )
        if (typedArray.hasValue(R.styleable.TabLayout_tabTextColor)) {
            this.tabTextColors =
                context.getColorStateList(typedArray, R.styleable.TabLayout_tabTextColor)
        }

        this.tabTextSize = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabTextSize, 0)
        this.tabIconTint = context.getColorStateList(typedArray, R.styleable.TabLayout_tabIconTint)
        this.tabIconTintMode = parseTintMode(
            typedArray.getInt(R.styleable.TabLayout_tabIconTintMode, -1),
            null as PorterDuff.Mode?
        )
        this.tabScaleTransitionScroll =
            typedArray.getFloat(R.styleable.TabLayout_tabScaleTransitionScroll, 1.0f)
        this.tabColorTransitionScroll =
            typedArray.getBoolean(R.styleable.TabLayout_tabTextColorTransitionScroll, false)
        this.tabTextBold = typedArray.getBoolean(R.styleable.TabLayout_tabTextBold, false)
        this.tabRippleColor = typedArray.getColor(R.styleable.TabLayout_tabRippleColor, 0)
        this.tabBackgroundResId = typedArray.getResourceId(R.styleable.TabLayout_tabBackground, 0)
        this.contentInsetStart =
            typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabContentStart, 0)
        this.inlineLabel = typedArray.getBoolean(R.styleable.TabLayout_tabInlineLabel, false)
        this.unboundedRipple =
            typedArray.getBoolean(R.styleable.TabLayout_tabUnboundedRipple, false)

        this.tabDividerWidth =
            typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabDividerWidth, 0)
        this.tabDividerHeight =
            typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabDividerHeight, 0)
        this.tabDividerColor = typedArray.getColor(R.styleable.TabLayout_tabDividerColor, 0)

        this.itemDecoration = if (tabDividerWidth > 0) TabView.DefItemDecoration(
            tabDividerWidth,
            tabDividerHeight
        ).apply {
            color = tabDividerColor
        } else null

        typedArray.recycle()

        setBackgroundResource(android.R.color.holo_green_dark)
    }

    private fun initTabLayoutManager(layoutManager: LayoutManager?) {
        removeAllViews()
        layoutManager?.init(this)
        super.addView(layoutManager?.slidingTabLayout,LayoutParams(-2,-2))
    }


    /**
     * Show Badge
     *
     * @param position
     */
    fun showBadgeMsg(position: Int) {
        showBadgeMsg(position, "", true)
    }

    /**
     * Show Badge
     *
     * @param position
     * @param count
     */
    fun showBadgeMsg(position: Int, count: Int) {
        showBadgeMsg(position, count.toString() + "", count > 0)
    }

    fun showBadgeMsg(position: Int, msg: String) {
        showBadgeMsg(position, msg, msg.trim { it <= ' ' }.isNotEmpty())
    }

    /**
     * Show Badge
     *
     * @param position
     * @param msg
     * @param showDot
     */
    fun showBadgeMsg(
        position: Int,
        msg: String?,
        showDot: Boolean
    ) {
        tabs.forEach {
            if (showDot) {
                it.showBadge(msg)
            } else {
                it.hideBadge()
            }
        }
    }

    /**
     * Setting the font color of Badge
     *
     * @param color
     */
    fun setBadgeTextColor(@ColorInt color: Int) {
        tabs.forEach {
            setBadgeTextColor(it.position, color)
        }
    }

    /**
     * Set the font color of the specified Badge
     *
     * @param position
     * @param color
     */
    fun setBadgeTextColor(position: Int, @ColorInt color: Int) {
        val tab: Tab? = tabs[position]
        tab?.setBadgeTextColor(color)
    }

    /**
     * Set the font size of the specified Badge
     *
     * @param position
     * @param textSize
     */
    fun setBadgeTextSize(position: Int, textSize: Int) {
        val tab: Tab? = tabs[position]
        tab?.setBadgeTextSize(textSize.toFloat())
    }

    fun setBadgeTextSize(textSize: Int) {
        tabs.forEach {
            setBadgeTextSize(it.position, textSize)
        }
    }

    /**
     * Setting the background color of Badge
     *
     * @param color
     */
    fun setBadgeColor(@ColorInt color: Int) {
        tabs.forEach {
            setBadgeColor(it.position, color)
        }
    }

    /**
     * Set the background color of the specified Badge
     *
     * @param position
     * @param color
     */
    fun setBadgeColor(position: Int, @ColorInt color: Int) {
        val tab: Tab? = tabs[position]
        tab?.setBadgeBackgroundColor(color)
    }

    /**
     * Set the border and color of the Badge
     *
     * @param width
     * @param color
     */
    fun setBadgeStroke(width: Int, @ColorInt color: Int) {
        tabs.forEach {
            setBadgeStroke(it.position, width, color)
        }
    }

    /**
     * Set the border and color of the specified Badge
     *
     * @param width
     * @param color
     */
    fun setBadgeStroke(position: Int, width: Int, @ColorInt color: Int) {
        val tab: Tab? = tabs[position]
        if (tab is Tab) {
            tab.setBadgeStroke(width, color)
        }
    }

    fun setTabTextColors(normalColor: Int, selectedColor: Int) {
        this.tabTextColors = createColorStateList(normalColor, selectedColor)
    }


    fun setTabIconTintResource(@ColorRes iconTintResourceId: Int) {
        this.tabIconTint = AppCompatResources.getColorStateList(
            this.context,
            iconTintResourceId
        )
    }


    fun setTabRippleColorResource(@ColorRes tabRippleColorId: Int) {
        this.tabRippleColor = resources.getColor(tabRippleColorId)
    }

    fun setTabIndicator(@DrawableRes tabSelectedIndicatorResourceId: Int) {
        this.tabIndicator = AppCompatResources.getDrawable(
            this.context,
            tabSelectedIndicatorResourceId
        )
    }

    @JvmOverloads
    fun addTab(tab: Tab, position: Int = tabs.size, setSelected: Boolean = tabs.isEmpty()) {
        require(!(tab.tabLayout !== this)) { "Tab belongs to a different TabLayout." }
        this.configureTab(tab, position)
        this.addTabView(tab)
        if (setSelected) {
            tab.select()
        }
    }

    private fun configureTab(tab: Tab, position: Int) {
        tab.position = position

        tab.tabTitleColors = tab.tabTitleColors ?: tabTextColors

        tab.tabIconTint = tab.tabIconTint ?: tabIconTint

        tab.tabIconTintMode = tab.tabIconTintMode ?: tabIconTintMode

        tab.tabTitleSize = if (tab.tabTitleSize != 0f) tab.tabTitleSize else tabTextSize.toFloat()

        tab.tabBackgroundResId =
            if (tab.tabBackgroundResId != 0) tab.tabBackgroundResId else tabBackgroundResId

        tab.tabRippleColor = if (tab.tabRippleColor != 0) tab.tabRippleColor else tabRippleColor

        tab.inlineLabel = inlineLabel

        tab.isTabTitleBold = tabTextBold

        tab.setTabPadding(
            tabPaddingStart,
            tabPaddingTop,
            tabPaddingEnd,
            tabPaddingBottom
        )

        if (position > 0) {
            tab.tabDecoration = tab.tabDecoration ?: itemDecoration
        }

        tabs.add(position, tab)
        for (i in position + 1 until tabs.size) {
            tabs[i].position = i
        }
    }

    private fun addTabView(tab: Tab) {
        this.layoutManager?.addView(
            tab.view,
            tab.position
        )
    }

    fun removeTab(tab: Tab) {
        require(!(tab.tabLayout !== this)) { "Tab does not belong to this TabLayout." }
        removeTabAt(tab.position)
    }

    fun removeTabAt(position: Int) {
        val selectedTabPosition = this.selectedTab?.position ?: 0
        this.removeTabViewAt(position)
        val removedTab: Tab? = tabs.removeAt(position)
        if (removedTab != null) {
            removedTab.reset()
            this.tabPool.release(removedTab)
        }
        val newTabCount = tabs.size
        for (i in position until newTabCount) {
            tabs[i].position = i
        }
        if (selectedTabPosition == position) {
            if (tabs.isEmpty()) {
                this.selectedTab = null
            } else {
                selectTab(tabs[0.coerceAtLeast(position - 1)])
            }
        }
    }

    private fun removeTabViewAt(position: Int) {
        this.layoutManager?.removeViewAt(position)
        requestLayout()
    }

    fun removeAllTabs() {
        this.layoutManager?.removeAllViews()
        val i: MutableIterator<*> = tabs.iterator()
        while (i.hasNext()) {
            val tab: Tab = i.next() as Tab
            i.remove()
            tab.reset()
            this.tabPool.release(tab)
        }
        this.selectedTab = null
    }


    fun newTab(): Tab {
        val tab = createTabFromPool()
        tab.tabLayout = this@TabLayout
        return tab
    }

    private fun createTabFromPool(): Tab {
        return tabPool.acquire() ?: TabView(context)
    }

    fun getTabCount(): Int {
        return tabs.size
    }

    fun getTabAt(index: Int): Tab? {
        return tabs[index]
    }

    fun getSelectedTabPosition(): Int {
        return selectedTab?.position ?: -1
    }

    override fun getChildAt(index: Int): View? {
        return layoutManager?.getChildAt(index)?.view
    }

    @JvmOverloads
    fun selectTab(tab: Tab, updateIndicator: Boolean = true, isCallback: Boolean = true) {
        val currentTab = selectedTab
        if (currentTab === tab) {
            this.dispatchTabReselected(tab)
            this.animateToTab(tab.position)
        } else {
            val newPosition = tab.position
            if (updateIndicator) {
                if ((currentTab == null || currentTab.position == -1) && newPosition != -1) {
                    this.setScrollPosition(newPosition, 0.0f, true)
                } else {
                    this.animateToTab(newPosition)
                }
                if (newPosition != -1) {
                    this.setSelectedTabView(newPosition)
                }
            }
            selectedTab = tab
            if (currentTab != null) {
                this.dispatchTabUnselected(currentTab)
            }
            this.dispatchTabSelected(tab)
        }
    }

    private fun setSelectedTabView(position: Int) {
        layoutManager?.setSelectedTabView(position)
    }

    private fun dispatchTabSelected(tab: Tab) {
        selectedListeners.forEach {
            it.onTabSelected(tab)
        }
    }

    private fun dispatchTabUnselected(tab: Tab) {
        selectedListeners.forEach {
            it.onTabUnselected(tab)
        }
    }

    private fun dispatchTabReselected(tab: Tab) {
        selectedListeners.forEach {
            it.onTabReselected(tab)
        }
    }

    private fun animateToTab(position: Int) {
        if (position != -1) {
            layoutManager?.animateToTab(position)
        }
    }

    @JvmOverloads
    fun setScrollPosition(
        position: Int,
        positionOffset: Float,
        updateSelectedText: Boolean,
        updateIndicatorPosition: Boolean = true
    ) {
        layoutManager?.setScrollPosition(
            position,
            positionOffset,
            updateSelectedText,
            updateIndicatorPosition
        )
    }

    abstract class LayoutManager {

        val slidingTabLayout by lazy { getSlidingLayout() }

        lateinit var tabLayout: TabLayout
            private set

        abstract val tabChildContent: ViewGroup

        protected abstract fun getSlidingLayout(): View

        abstract fun addView(view: View, position: Int)

        fun init(tabLayout: TabLayout) {
            this.tabLayout = tabLayout
        }

        abstract fun getChildAt(position: Int): Tab?

        abstract fun removeViewAt(position: Int)

        abstract fun removeAllViews()

        abstract fun animateToTab(position: Int)

        abstract fun setScrollPosition(
            position: Int,
            offset: Float,
            updateSelectedText: Boolean,
            updateIndicatorPosition: Boolean
        )

        abstract fun setSelectedTabView(position: Int)
        abstract fun postInvalidateOnAnimation()

    }

    interface OnTabSelectedListener : BaseOnTabSelectedListener

    interface BaseOnTabSelectedListener {
        fun onTabSelected(item: Tab)
        fun onTabUnselected(item: Tab)
        fun onTabReselected(item: Tab)
    }
}
