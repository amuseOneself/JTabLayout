package com.liang.tablayout3x

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import com.liang.tablayout3x.indicator.IndicatorHelper
import com.liang.utils.AnimationUtils
import com.liang.widget.TabLayout
import kotlin.math.roundToInt


class GridLayoutManager(private val context: Context, private val spanCount: Int) :
    TabLayout.LayoutManager() {

    private lateinit var slidingGridTabLayout: SlidingGridTabLayout

    private lateinit var contentTabLayout: View

    private var indicatorHelper: IndicatorHelper = IndicatorHelper(this)

    private val scrollAnimator by lazy {
        ValueAnimator().apply {
            duration = tabLayout.tabAnimationDuration
            interpolator = AnimationUtils.accelerateDecelerateInterpolator
        }
    }

    override val tabChildContent: ViewGroup
        get() = slidingGridTabLayout

    override fun getSlidingLayout(): View {
        contentTabLayout = ScrollView(context).apply {
            setBackgroundResource(R.color.holo_red)
            slidingGridTabLayout = SlidingGridTabLayout(context)
            addView(slidingGridTabLayout)
        }
        return contentTabLayout
    }

    override fun addView(view: View, position: Int) {
        slidingGridTabLayout.addView(view, LinearLayout.LayoutParams(-1, -2))
    }

    override fun getChildAt(position: Int): Tab = slidingGridTabLayout.getChildAt(position) as Tab

    override fun removeViewAt(position: Int) {
        slidingGridTabLayout.removeViewAt(position)
    }

    override fun removeAllViews() {
        for (i in this.slidingGridTabLayout.childCount - 1 downTo 0) {
            this.removeViewAt(i)
        }
    }

    override fun animateToTab(position: Int) {
        if (tabLayout.windowToken != null && ViewCompat.isLaidOut(tabLayout) && !this.slidingGridTabLayout.childrenNeedLayout()) {
            when (tabChildContent) {
                is ScrollView -> {
                    animateToTab(position, tabChildContent as ScrollView)
                }
            }
            this.slidingGridTabLayout.animateIndicatorToPosition(
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
        val selectedChild = this.slidingGridTabLayout.getChildAt(position)
        val nextChild = this.slidingGridTabLayout.getChildAt(position + 1)
        val selectedHeight = selectedChild?.height ?: 0
        val nextHeight = nextChild?.height ?: 0
        val scrollBase = selectedChild.top + selectedHeight / 2 - tabLayout.height / 2
        val scrollOffset = ((selectedHeight + nextHeight).toFloat() * 0.5f * offset)
        return if (ViewCompat.getLayoutDirection(tabLayout) == 0) (scrollBase + scrollOffset).roundToInt() else (scrollBase - scrollOffset).roundToInt()
    }

    override fun setScrollPosition(
        position: Int,
        offset: Float,
        updateSelectedText: Boolean,
        updateIndicatorPosition: Boolean
    ) {
        val roundedPosition = (position.toFloat() + offset).roundToInt()
        if (roundedPosition >= 0 && roundedPosition < this.slidingGridTabLayout.childCount) {
            if (updateIndicatorPosition) {
                this.slidingGridTabLayout.setIndicatorPositionFromTabPosition(
                    position,
                    offset
                )
            }
            if (scrollAnimator.isRunning) {
                scrollAnimator.cancel()
            }

            if (tabChildContent is ScrollView) {
                (tabChildContent as ScrollView).scrollTo(
                    0,
                    calculateScrollYForTab(position, offset)
                )
            }

            if (updateSelectedText) {
                setSelectedTabView(roundedPosition)
            }
        }
    }

    override fun setSelectedTabView(position: Int) {
        val tabCount: Int = this.slidingGridTabLayout.childCount
        if (position < tabCount) {
            for (i in 0 until tabCount) {
                val child: View = this.slidingGridTabLayout.getChildAt(i)
                child.isSelected = i == position
                child.isActivated = i == position
            }
        }
    }

    override fun postInvalidateOnAnimation() {
        ViewCompat.postInvalidateOnAnimation(slidingGridTabLayout)
    }

    @SuppressLint("NewApi")
    private inner class SlidingGridTabLayout(context: Context) : LinearLayout(context) {

        init {
            setWillNotDraw(false)
            clipToOutline = tabLayout.tabUseClipToOutline
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

//
//        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
////            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
////            val width = MeasureSpec.getSize(widthMeasureSpec)
////            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
////            val height = MeasureSpec.getSize(heightMeasureSpec)
////            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
////
////            val childLazyWidth = (width - paddingLeft - paddingRight) / spanCount
////
////            val size: IntArray = measureChildrenWithGild(
////                widthMeasureSpec,
////                heightMeasureSpec,
////                childLazyWidth
////            )
////
////            Log.e(javaClass.simpleName, "onMeasure: $size")
////
////            setMeasuredDimension(
////                if (widthMode == MeasureSpec.EXACTLY) width else size[0] + paddingLeft + paddingRight,
////                if (heightMode == MeasureSpec.EXACTLY) height else size[1] + paddingTop + paddingBottom
////            )
//        }

//        override fun onLayout(
//            changed: Boolean,
//            l: Int,
//            t: Int,
//            r: Int,
//            b: Int
//        ) {
//            super.onLayout(changed, l, t, r, b)
//            for (i in 0 until childCount) {
//                val child = getChildAt(i)
//                if (child.visibility == View.GONE) {
//                    continue
//                }
//                child.layout(child.left, child.top, child.right, child.bottom)
//            }
//            indicatorHelper.refresh()
//        }

        private fun measureChildrenWithGild(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int,
            childLazyWidth: Int
        ): IntArray {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            var maxWidth = 0
            var maxHeight = 0
            var maxChildWidth = 0
            var maxChildHeight = 0
            val mCount = childCount
            for (i in 0 until mCount) {
                val child = getChildAt(i)
                if (child.visibility == View.GONE) {
                    continue
                }
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                val childHeight = child.measuredHeight
                val childWidth = child.measuredWidth

                if (maxChildWidth + childLazyWidth > width - paddingLeft - paddingRight) {
                    maxWidth = childLazyWidth.coerceAtLeast(maxChildWidth)
                    maxChildWidth = childLazyWidth
                    maxHeight += maxChildHeight
                    child.left = paddingLeft
                    maxChildHeight = childHeight
                } else {
                    child.left = maxChildWidth
                    maxChildWidth += childLazyWidth
                    maxChildHeight = maxChildHeight.coerceAtLeast(childHeight)
                }
                child.top = maxHeight
                if (i == mCount - 1) {
                    maxWidth = maxChildWidth.coerceAtLeast(maxWidth)
                    maxHeight += maxChildHeight
                }
                child.right = child.left + childWidth
                child.bottom = child.top + childHeight
            }
            return intArrayOf(maxWidth, maxHeight)
        }
    }

}