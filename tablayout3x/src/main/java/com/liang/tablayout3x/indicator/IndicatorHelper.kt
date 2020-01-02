package com.liang.tablayout3x.indicator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.RectF
import com.liang.tablayout3x.Tab
import com.liang.utils.AnimationUtils
import com.liang.utils.dpToPx
import com.liang.widget.TabLayout


class IndicatorHelper(private val layoutManager: TabLayout.LayoutManager) {

    private val indicatorPoint = IndicatorPoint()
    private var indicatorAnimator: ValueAnimator? = null
    private val tabLayout by lazy { layoutManager.tabLayout }
    private val tabChildContent by lazy { layoutManager.tabChildContent }
    var selectedPosition = -1
    var selectionOffset = 0f

    private fun cancelAnimator() {
        indicatorAnimator?.let {
            if (it.isRunning) {
                it.cancel()
            }
        }
    }

    private fun calculateTabViewContentBounds(
        tab: Tab,
        contentBounds: RectF
    ) {
        if (tabLayout.tabIndicatorWidth > 0 || tabLayout.tabIndicatorWidthScale > 0) {
            val left: Int = tab.view.left
            val right: Int = tab.view.right
            val tabWidth = right - left
            tabLayout.tabIndicatorWidth =
                tabWidth.coerceAtMost(if (tabLayout.tabIndicatorWidth > 0) tabLayout.tabIndicatorWidth else (tabWidth * tabLayout.tabIndicatorWidthScale).toInt())
            val tabIndicatorInt = 0.coerceAtLeast(tabWidth - tabLayout.tabIndicatorWidth) / 2
            val contentLeftBounds = left + tabIndicatorInt
            val contentRightBounds = contentLeftBounds + tabLayout.tabIndicatorWidth
            contentBounds[contentLeftBounds.toFloat(), 0.0f, contentRightBounds.toFloat()] =
                0.0f
        } else {
            var tabViewContentWidth: Int = tab.contentWidth
            if (tabViewContentWidth < tabLayout.context.dpToPx(24f)) {
                tabViewContentWidth = tabLayout.context.dpToPx(24f)
            }
            val tabViewCenter: Int =
                (tab.view.left + tab.view.right) / 2
            val contentLeftBounds = tabViewCenter - tabViewContentWidth / 2
            val contentRightBounds = tabViewCenter + tabViewContentWidth / 2
            contentBounds[contentLeftBounds.toFloat(), 0.0f, contentRightBounds.toFloat()] =
                0.0f
        }
    }

    private fun setIndicatorPosition(left: Float, right: Float) {
        if (left != indicatorPoint.left || right != indicatorPoint.right) {
            indicatorPoint.left = left
            indicatorPoint.right = right
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
            val target = IndicatorPoint()
            target.left = targetView.left.toFloat()
            target.right = targetView.right.toFloat()
            if (!tabLayout.tabIndicatorFullWidth && targetView is Tab) {
                calculateTabViewContentBounds(
                    (targetView as Tab),
                    tabLayout.tabViewContentBounds
                )
                target.left = tabLayout.tabViewContentBounds.left
                target.right = tabLayout.tabViewContentBounds.right
            }
            if (indicatorPoint != target) {
                indicatorAnimator = ValueAnimator.ofObject(
                    if (tabLayout.tabIndicatorTransitionScroll) TransitionIndicatorEvaluator() else DefIndicatorEvaluator(),
                    indicatorPoint,
                    target
                ).apply {
                    this.interpolator = AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR
                    this.duration = duration
                    this.addUpdateListener { animator ->
                        val p: IndicatorPoint = animator.animatedValue as IndicatorPoint
                        setIndicatorPosition(p.left, p.right)
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
        var left: Int
        var right: Int
        if (selectedTab != null && selectedTab.width > 0) {
            left = selectedTab.left
            right = selectedTab.right
            if (!tabLayout.tabIndicatorFullWidth && selectedTab is Tab) {
                calculateTabViewContentBounds(
                    (selectedTab as Tab),
                    tabLayout.tabViewContentBounds
                )
                left = tabLayout.tabViewContentBounds.left.toInt()
                right = tabLayout.tabViewContentBounds.right.toInt()
            }
            if (selectionOffset > 0.0f && selectedPosition < tabChildContent.childCount - 1) {
                val nextTitle = tabChildContent.getChildAt(selectedPosition + 1)
                var nextTitleLeft = nextTitle.left
                var nextTitleRight = nextTitle.right
                if (!tabLayout.tabIndicatorFullWidth && nextTitle is Tab) {
                    calculateTabViewContentBounds(
                        (nextTitle as Tab),
                        tabLayout.tabViewContentBounds
                    )
                    nextTitleLeft = tabLayout.tabViewContentBounds.left.toInt()
                    nextTitleRight = tabLayout.tabViewContentBounds.right.toInt()
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
                    left += ((nextTitleLeft - left) * offL).toInt()
                    right += ((nextTitleRight - right) * offR).toInt()
                } else {
                    left =
                        (selectionOffset * nextTitleLeft.toFloat() + (1.0f - selectionOffset) * left.toFloat()).toInt()
                    right =
                        (selectionOffset * nextTitleRight.toFloat() + (1.0f - selectionOffset) * right.toFloat()).toInt()
                }
            }
        } else {
            right = -1
            left = -1
        }

        setIndicatorPosition(left.toFloat(), right.toFloat())
    }

    fun drawIndicator(canvas: Canvas) {
        val selectedIndicator = tabLayout.tabSelectedIndicator
        selectedIndicator?.let { indicator ->
            var indicatorHeight = indicator.intrinsicHeight

            if (tabLayout.tabIndicatorHeight > 0) {
                indicatorHeight = tabLayout.tabIndicatorHeight
            }

            var indicatorTop = 0
            var indicatorBottom = 0
            when (tabLayout.tabIndicatorGravity) {
                0 -> {
                    indicatorTop = tabLayout.height - indicatorHeight - tabLayout.tabIndicatorMargin
                    indicatorBottom = tabLayout.height - tabLayout.tabIndicatorMargin
                }
                1 -> {
                    indicatorTop = (tabLayout.height - indicatorHeight) / 2
                    indicatorBottom = (tabLayout.height + indicatorHeight) / 2
                }
                2 -> {
                    indicatorTop = tabLayout.tabIndicatorMargin
                    indicatorBottom = indicatorHeight
                }
                3 -> {
                    indicatorTop = tabLayout.tabIndicatorMargin
                    indicatorBottom = tabLayout.height - tabLayout.tabIndicatorMargin
                }
            }

            if (indicatorPoint.left >= 0 && indicatorPoint.right > indicatorPoint.left) {
                indicator.setBounds(
                    indicatorPoint.left.toInt(),
                    indicatorTop,
                    indicatorPoint.right.toInt(),
                    indicatorBottom
                )

                indicator.draw(canvas)
            }
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