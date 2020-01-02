package com.liang.widget

import android.content.Context
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.support.v4.util.Pools
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.liang.tablayout3x.R
import com.liang.tablayout3x.Tab


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
    }

    var tabIndicatorGravity = 0
    var tabSelectedIndicator: Drawable? = null
    private val tabs = arrayListOf<Tab>()
    private val tabPool: Pools.Pool<Tab> = Pools.SynchronizedPool(16)
    private val selectedListeners by lazy { arrayListOf<BaseOnTabSelectedListener>() }
    private var selectedTab: Tab? = null

    var orientation: Int = Horizontal

    var layoutManager: LayoutManager? = null
        set(value) {
            if (value != field) {
                field = value
                initTabLayoutManager(field)
            }
        }

    var tabIndicatorFullWidth = false

    var tabAnimationDuration = 300L

    val tabViewContentBounds = RectF()

    var tabIndicatorTier = 0

    var tabIndicatorWidth = 0

    var tabIndicatorHeight = 0

    var tabIndicatorMargin = 0

    var tabIndicatorWidthScale = 0f

    var tabIndicatorTransitionScroll = false

    init {
        initAttrs(attrs, defStyleAttr)
    }


    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyleAttr, 0)

        orientation = typedArray.getInt(R.styleable.TabLayout_android_orientation, Horizontal)

        typedArray.recycle()

        setBackgroundResource(android.R.color.holo_green_dark)
    }

    private fun initTabLayoutManager(layoutManager: LayoutManager?) {
        removeAllViews()
        layoutManager?.init(this)
        super.addView(layoutManager?.slidingTabLayout)
//        layoutManager = when (tabLayoutMode) {
//            Grid -> GridLayoutManager(this)
//            Flow -> FlowLayoutManager(this)
//            else -> LinearLayoutManager(this)
//        }

//        repeat(100) {
//            layoutManager.addView(TextView(context).apply {
//                text = "addView: $it"
//                gravity = Gravity.CENTER
//                setBackgroundResource(android.R.color.holo_orange_dark)
//            }, LayoutParams(300, 200))
//        }
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

        abstract fun getChildAt(position: Int): Tab

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
