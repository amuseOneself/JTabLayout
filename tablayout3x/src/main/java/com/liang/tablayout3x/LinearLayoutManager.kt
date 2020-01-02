package com.liang.tablayout3x

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.support.v4.view.ViewCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.liang.tablayout3x.indicator.IndicatorHelper
import com.liang.utils.AnimationUtils
import com.liang.widget.TabLayout
import kotlin.math.roundToInt


class LinearLayoutManager @JvmOverloads constructor(
    private val context: Context,
    private val orientation: Int = TabLayout.Horizontal,
    private val mode: Int = TabLayout.ModeFixed
) : TabLayout.LayoutManager() {

    private lateinit var contentTabLayout: View

    private lateinit var slidingLinearTabLayout: SlidingLinearTabLayout

    private var indicatorHelper: IndicatorHelper = IndicatorHelper(this)

    private val scrollAnimator by lazy {
        ValueAnimator().apply {
            duration = tabLayout.tabAnimationDuration
            interpolator = AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR
        }
    }

    override val tabChildContent: ViewGroup
        get() = slidingLinearTabLayout //To change initializer of created properties use File | Settings | File Templates.

    @SuppressLint("NewApi")
    override fun getSlidingLayout(): View {
        contentTabLayout = (if (orientation == TabLayout.Vertical) ScrollView(context)
        else HorizontalScrollView(context)).apply {
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            slidingLinearTabLayout = SlidingLinearTabLayout(context)
            addView(slidingLinearTabLayout.apply {
                orientation = this@LinearLayoutManager.orientation
            })
        }

        return contentTabLayout
    }

    override fun addView(view: View, position: Int) {
        slidingLinearTabLayout.addView(view, createLayoutParamsForTabs())
    }

    override fun getChildAt(position: Int): Tab = slidingLinearTabLayout.getChildAt(position) as Tab

    override fun removeViewAt(position: Int) {
        slidingLinearTabLayout.removeViewAt(position)
    }

    override fun removeAllViews() {
        for (i in this.slidingLinearTabLayout.childCount - 1 downTo 0) {
            this.removeViewAt(i)
        }
    }

    override fun animateToTab(position: Int) {
        if (tabLayout.windowToken != null && ViewCompat.isLaidOut(tabLayout) && !this.slidingLinearTabLayout.childrenNeedLayout()) {
            when (contentTabLayout) {
                is ScrollView -> {
                    animateToTab(position, contentTabLayout as ScrollView)
                }
                is HorizontalScrollView -> {
                    animateToTab(position, contentTabLayout as HorizontalScrollView)
                }
            }
            this.slidingLinearTabLayout.animateIndicatorToPosition(
                position,
                tabLayout.tabAnimationDuration
            )
        } else {
            setScrollPosition(
                position, 0.0f,
                updateSelectedText = true,
                updateIndicatorPosition = true
            )
        }
    }

    private fun animateToTab(position: Int, scrollView: ScrollView) {
        val startScrollY: Int = scrollView.scrollY
        val targetScrollY: Int = this.calculateScrollYForTab(position, 0.0f)
        if (startScrollY != targetScrollY) {
            scrollAnimator.addUpdateListener {
                scrollView.scrollTo(0, it.animatedValue as Int)
            }
            this.scrollAnimator.setIntValues(startScrollY, targetScrollY)
            this.scrollAnimator.start()
        }
    }


    private fun calculateScrollYForTab(position: Int, offset: Float): Int {
        return if (mode == 0) {
            val selectedChild = this.slidingLinearTabLayout.getChildAt(position)
            val nextChild = this.slidingLinearTabLayout.getChildAt(position + 1)
            val selectedHeight = selectedChild?.height ?: 0
            val nextHeight = nextChild?.height ?: 0
            val scrollBase = selectedChild.top + selectedHeight / 2 - tabLayout.height / 2
            val scrollOffset = ((selectedHeight + nextHeight).toFloat() * 0.5f * offset)
            if (ViewCompat.getLayoutDirection(tabLayout) == 0) (scrollBase + scrollOffset).roundToInt() else (scrollBase - scrollOffset).roundToInt()
        } else 0
    }


    private fun animateToTab(position: Int, horizontalView: HorizontalScrollView) {
        val startScrollX: Int = horizontalView.scrollX
        val targetScrollX: Int = this.calculateScrollXForTab(position, 0.0f)
        if (startScrollX != targetScrollX) {
            scrollAnimator.addUpdateListener {
                horizontalView.scrollTo(it.animatedValue as Int, 0)
            }
            this.scrollAnimator.setIntValues(startScrollX, targetScrollX)
            this.scrollAnimator.start()
        }
    }

    private fun calculateScrollXForTab(position: Int, offset: Float): Int {
        return if (mode == 0) {
            val selectedChild = this.slidingLinearTabLayout.getChildAt(position)
            val nextChild = this.slidingLinearTabLayout.getChildAt(position + 1)
            val selectedWidth = selectedChild?.width ?: 0
            val nextWidth = nextChild?.width ?: 0
            val scrollBase = selectedChild.left + selectedWidth / 2 - tabLayout.width / 2
            val scrollOffset = ((selectedWidth + nextWidth).toFloat() * 0.5f * offset)
            if (ViewCompat.getLayoutDirection(tabLayout) == 0) (scrollBase + scrollOffset).roundToInt() else (scrollBase - scrollOffset).roundToInt()
        } else 0
    }

    override fun setScrollPosition(
        position: Int,
        offset: Float,
        updateSelectedText: Boolean,
        updateIndicatorPosition: Boolean
    ) {
        val roundedPosition = (position.toFloat() + offset).roundToInt()
        if (roundedPosition >= 0 && roundedPosition < this.slidingLinearTabLayout.childCount) {
            if (updateIndicatorPosition) {
                this.slidingLinearTabLayout.setIndicatorPositionFromTabPosition(
                    position,
                    offset
                )
            }
            if (scrollAnimator.isRunning) {
                scrollAnimator.cancel()
            }

            when (contentTabLayout) {
                is ScrollView -> {
                    animateToTab(position, contentTabLayout as ScrollView)
                    (contentTabLayout as ScrollView).scrollTo(
                        0,
                        calculateScrollYForTab(position, offset)
                    )

                }
                is HorizontalScrollView -> {
                    animateToTab(position, contentTabLayout as HorizontalScrollView)
                    (contentTabLayout as HorizontalScrollView).scrollTo(
                        calculateScrollXForTab(
                            position,
                            offset
                        ), 0
                    )
                }
            }

            if (updateSelectedText) {
                setSelectedTabView(roundedPosition)
            }
        }
    }

    override fun setSelectedTabView(position: Int) {
        val tabCount: Int = this.slidingLinearTabLayout.childCount
        if (position < tabCount) {
            for (i in 0 until tabCount) {
                val child: View = this.slidingLinearTabLayout.getChildAt(i)
                child.isSelected = i == position
                child.isActivated = i == position
            }
        }
    }

    override fun postInvalidateOnAnimation() {
        ViewCompat.postInvalidateOnAnimation(slidingLinearTabLayout)
    }

    private inner class HorizontalScrollView(context: Context?) :
        android.widget.HorizontalScrollView(context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            if (childCount == 1) {
                val child = getChildAt(0)
                var remeasure = false
                when (mode) {
                    TabLayout.ModeScrollable -> remeasure =
                        child.measuredWidth < measuredWidth
                    TabLayout.ModeFixed -> remeasure =
                        child.measuredWidth != measuredWidth
                }
                if (remeasure) {
                    val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        measuredWidth, MeasureSpec.EXACTLY
                    )
                    child.measure(childWidthMeasureSpec, heightMeasureSpec)
                }
            }
        }
    }


    private inner class ScrollView(context: Context?) : android.widget.ScrollView(context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            if (childCount == 1) {
                val child = getChildAt(0)
                var remeasure = false
                when (mode) {
                    TabLayout.ModeScrollable -> remeasure =
                        child.measuredHeight < measuredHeight
                    TabLayout.ModeFixed -> remeasure =
                        child.measuredHeight != measuredHeight
                }

                if (remeasure) {
                    val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        measuredHeight, MeasureSpec.EXACTLY
                    )
                    child.measure(widthMeasureSpec, childHeightMeasureSpec)
                }
            }
        }
    }


    private inner class SlidingLinearTabLayout(context: Context) : LinearLayout(context) {

        override fun onLayout(
            changed: Boolean,
            l: Int,
            t: Int,
            r: Int,
            b: Int
        ) {
            super.onLayout(changed, l, t, r, b)
            indicatorHelper.refresh()
        }

        fun childrenNeedLayout(): Boolean {
            var i = 0
            val z = this.childCount
            while (i < z) {
                val child = this.getChildAt(i)
                if (child.width <= 0) {
                    return true
                }
                ++i
            }
            return false
        }

        fun animateIndicatorToPosition(position: Int, duration: Long) {
            indicatorHelper.animateIndicatorToPosition(position, duration)
        }

        fun setIndicatorPositionFromTabPosition(position: Int, offset: Float) {
            indicatorHelper.setIndicatorPositionFromTabPosition(position, offset)
        }

        override fun draw(canvas: Canvas) {
            if (tabLayout.tabIndicatorTier == TabLayout.IndicatorTierBackground) {
                indicatorHelper.drawIndicator(canvas)
                super.draw(canvas)
            } else {
                super.draw(canvas)
                indicatorHelper.drawIndicator(canvas)
            }
        }

        init {
            setBackgroundResource(android.R.color.holo_blue_dark)
            setWillNotDraw(false)
            gravity = Gravity.CENTER
        }
    }


    private fun createLayoutParamsForTabs(): LinearLayout.LayoutParams {
        val lp = if (orientation == TabLayout.Horizontal) LinearLayout.LayoutParams(
            -2,
            -1
        ) else LinearLayout.LayoutParams(-1, -2)
        updateTabViewLayoutParams(lp)
        return lp
    }

    private fun updateTabViewLayoutParams(lp: LinearLayout.LayoutParams) {
        when (orientation) {
            TabLayout.Horizontal -> if (mode == TabLayout.ModeFixed) {
                lp.width = 0
                lp.weight = 1.0f
            } else {
                lp.width = -2
                lp.weight = 0.0f
            }

            TabLayout.Vertical -> if (mode == TabLayout.ModeFixed) {
                lp.height = 0
                lp.weight = 1.0f
            } else {
                lp.height = -2
                lp.weight = 0.0f
            }
        }

    }


}