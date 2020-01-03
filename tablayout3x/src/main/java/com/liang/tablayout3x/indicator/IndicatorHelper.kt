package com.liang.tablayout3x.indicator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.v4.graphics.drawable.DrawableCompat
import com.liang.tablayout3x.LinearLayoutManager
import com.liang.tablayout3x.Tab
import com.liang.utils.AnimationUtils
import com.liang.widget.TabLayout


class IndicatorHelper(private val layoutManager: TabLayout.LayoutManager) {

    private val indicatorPoint = Rect()
    private var indicatorAnimator: ValueAnimator? = null
    private val tabLayout by lazy { layoutManager.tabLayout }
    private val tabChildContent by lazy { layoutManager.tabChildContent }
    private var selectedPosition = -1
    private var selectionOffset = 0f
    //    private val tabContentBounds = Rect()
    private fun cancelAnimator() {
        indicatorAnimator?.let {
            if (it.isRunning) {
                it.cancel()
            }
        }
    }

    private fun calculateTabViewContentBounds(
        tab: Tab,
        contentBounds: Rect
    ) {
        if (tabLayout.tabIndicatorWidth > 0 || tabLayout.tabIndicatorWidthScale > 0) {
            val left: Int = tab.view.left
            val right: Int = tab.view.right
            val top: Int = tab.view.top
            var tabWidth = right - left

            if (layoutManager is LinearLayoutManager && layoutManager.orientation == 1) {
                val bottom: Int = tab.view.bottom
                tabWidth = bottom - top
            }

            tabLayout.tabIndicatorWidth =
                tabWidth.coerceAtMost(if (tabLayout.tabIndicatorWidth > 0) tabLayout.tabIndicatorWidth else (tabWidth * tabLayout.tabIndicatorWidthScale).toInt())

            val tabIndicatorInt = 0.coerceAtLeast(tabWidth - tabLayout.tabIndicatorWidth) / 2

            if (layoutManager is LinearLayoutManager && layoutManager.orientation == 1) {
                val contentTopBounds = top + tabIndicatorInt
                val contentBottomBounds = contentTopBounds + tabLayout.tabIndicatorWidth
                contentBounds[0, contentTopBounds, 0] = contentBottomBounds
            } else {
                val contentLeftBounds = left + tabIndicatorInt
                val contentRightBounds = contentLeftBounds + tabLayout.tabIndicatorWidth
                contentBounds[contentLeftBounds, 0, contentRightBounds] = 0
            }
        } else {
            val tabViewContentWidth: Int =
                if (layoutManager is LinearLayoutManager && layoutManager.orientation == 1) tab.contentHeight else tab.contentWidth

            val tabViewCenter: Int =
                if (layoutManager is LinearLayoutManager && layoutManager.orientation == 1) (tab.view.top + tab.view.bottom) / 2 else (tab.view.left + tab.view.right) / 2
            val contentLeftBounds = tabViewCenter - tabViewContentWidth / 2
            val contentRightBounds = tabViewCenter + tabViewContentWidth / 2

            if (layoutManager is LinearLayoutManager && layoutManager.orientation == 1) {
                contentBounds[0, contentLeftBounds, 0] = contentRightBounds
            } else {
                contentBounds[contentLeftBounds, 0, contentRightBounds] = 0
            }
        }
    }

    private fun measure(selectedIndicator: Drawable) {
        var indicatorHeight = selectedIndicator.intrinsicHeight

        if (tabLayout.tabIndicatorHeight > 0) {
            indicatorHeight = tabLayout.tabIndicatorHeight
        }

        if (tabLayout.tabIndicatorFullHeight) {
            indicatorHeight = if (layoutManager is LinearLayoutManager && layoutManager.orientation == 1) tabLayout.width else tabLayout.height
        }

        var indicatorStart = 0
        var indicatorEnd = 0

        val tabLayoutWidth =
            if (layoutManager is LinearLayoutManager && layoutManager.orientation == 1) tabLayout.width else tabLayout.height

        when (tabLayout.tabIndicatorGravity) {
            TabLayout.IndicatorGravityEnd -> {
                indicatorStart = tabLayoutWidth - indicatorHeight - tabLayout.tabIndicatorMargin
                indicatorEnd = indicatorStart + indicatorHeight
            }
            TabLayout.IndicatorGravityCenter -> {
                indicatorStart = (tabLayoutWidth - indicatorHeight) / 2
                indicatorEnd = (tabLayoutWidth + indicatorHeight) / 2
            }
            TabLayout.IndicatorGravityStart -> {
                indicatorStart = tabLayout.tabIndicatorMargin
                indicatorEnd = indicatorStart + indicatorHeight
            }
        }

        if (layoutManager is LinearLayoutManager && layoutManager.orientation == 1) {
            indicatorPoint.left = indicatorStart
            indicatorPoint.right = indicatorEnd
        } else {
            indicatorPoint.top = indicatorStart
            indicatorPoint.bottom = indicatorEnd
        }

    }

    private fun setIndicatorPosition(rect: Rect) {
        if (rect != indicatorPoint) {
            indicatorPoint.set(rect)
            layoutManager.postInvalidateOnAnimation()
        }
    }

    fun setIndicatorPositionFromTabPosition(position: Int, offset: Float) {
        cancelAnimator()
        this.selectedPosition = position
        this.selectionOffset = offset
        this.updateIndicatorPosition()
    }

    fun animateIndicatorToPosition(position: Int, duration: Long) {
        cancelAnimator()
        val targetView = tabChildContent.getChildAt(position)
        if (targetView == null) {
            updateIndicatorPosition()
        } else {
            val target = Rect()
            target.left = targetView.left
            target.right = targetView.right
            target.top = targetView.top
            target.bottom = targetView.bottom
            if (!tabLayout.tabIndicatorFullWidth && targetView is Tab) {
                calculateTabViewContentBounds(
                    (targetView as Tab),
                    target
                )
            }
            if (indicatorPoint != target) {
                indicatorAnimator = ValueAnimator.ofObject(
                    if (tabLayout.tabIndicatorTransitionScroll) TransitionIndicatorEvaluator() else DefIndicatorEvaluator(),
                    indicatorPoint,
                    target
                ).apply {
                    this.interpolator = AnimationUtils.accelerateDecelerateInterpolator
                    this.duration = duration
                    this.addUpdateListener { animator ->
                        val p = animator.animatedValue as Rect
                        setIndicatorPosition(p)
                    }
                    this.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animator: Animator) {
                            selectedPosition = position
                            selectionOffset = 0.0f
                        }
                    })
                }.also { it.start() }

            }
        }
    }

    private fun updateIndicatorPosition() {
        val selectedTab = tabChildContent.getChildAt(selectedPosition)
        val target = Rect()
        if (selectedTab != null && selectedTab.width > 0) {
            target.left = selectedTab.left
            target.right = selectedTab.right
            target.top = selectedTab.top
            target.bottom = selectedTab.bottom
            if (!tabLayout.tabIndicatorFullWidth && selectedTab is Tab) {
                calculateTabViewContentBounds(
                    (selectedTab as Tab),
                    target
                )
            }

            if (selectionOffset > 0.0f && selectedPosition < tabChildContent.childCount - 1) {
                val nextTitle = tabChildContent.getChildAt(selectedPosition + 1)
                var nextTitleLeft = nextTitle.left
                var nextTitleRight = nextTitle.right
                if (!tabLayout.tabIndicatorFullWidth && nextTitle is Tab) {
                    calculateTabViewContentBounds(
                        (nextTitle as Tab),
                        target
                    )
                    nextTitleLeft = target.left
                    nextTitleRight = target.right
                }
                if (tabLayout.tabIndicatorTransitionScroll) {
                    var offR = selectionOffset * 2 - 1
                    var offL = selectionOffset * 2
                    if (selectedPosition + 1 < selectedPosition && selectionOffset > 0) {
                        if (offR < 0) {
                            offR = 0f
                        }
                        if (1 - offL < 0) {
                            offL = 1f
                        }
                    } else {
                        offL = selectionOffset * 2 - 1
                        offR = selectionOffset * 2
                        if (offL < 0) {
                            offL = 0f
                        }
                        if (1 - offR < 0) {
                            offR = 1f
                        }
                    }
                    target.left += ((nextTitleLeft - target.left) * offL).toInt()
                    target.right += ((nextTitleRight - target.right) * offR).toInt()
                } else {
                    target.left =
                        (selectionOffset * nextTitleLeft.toFloat() + (1.0f - selectionOffset) * target.left.toFloat()).toInt()
                    target.right =
                        (selectionOffset * nextTitleRight.toFloat() + (1.0f - selectionOffset) * target.right.toFloat()).toInt()
                }
            }
        } else {
            target.right = -1
            target.left = -1
        }

        setIndicatorPosition(target)
    }

    fun drawIndicator(canvas: Canvas) {
        val selectedIndicator = tabLayout.tabIndicator ?: GradientDrawable()
        if (tabLayout.tabIndicatorColor != 0) {
            if (Build.VERSION.SDK_INT == 21) {
                selectedIndicator.setColorFilter(
                    tabLayout.tabIndicatorColor,
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                DrawableCompat.setTint(
                    selectedIndicator,
                    tabLayout.tabIndicatorColor
                )
            }
        }

        measure(selectedIndicator)

        if ((indicatorPoint.left >= 0 && indicatorPoint.right > indicatorPoint.left)
            || (indicatorPoint.top >= 0 && indicatorPoint.bottom > indicatorPoint.top)
        ) {
            selectedIndicator.bounds = indicatorPoint
            selectedIndicator.draw(canvas)
        }
    }

    fun refresh() {
        if (indicatorAnimator != null && indicatorAnimator!!.isRunning) {
            indicatorAnimator?.cancel()
            val duration = indicatorAnimator!!.duration
            animateIndicatorToPosition(
                selectedPosition,
                (((1.0f - indicatorAnimator!!.animatedFraction) * duration.toFloat()).toLong())
            )
        } else {
            updateIndicatorPosition()
        }
    }
}