package com.liang.tablayout3x

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import com.liang.widget.TabLayout


class LinearLayoutManager(tabLayout: TabLayout) : TabLayout.LayoutManager(tabLayout) {

    private lateinit var slidingLinearTabLayout: SlidingLinearTabLayout
    override fun getTabLayoutParams(): ViewGroup.LayoutParams = FrameLayout.LayoutParams(-2, -2)

    @SuppressLint("NewApi")
    override fun getSlidingLayout(): View =
        (if (tabLayout.orientation == TabLayout.Vertical) ScrollView(tabLayout.context)
        else HorizontalScrollView(tabLayout.context)).apply {
            //            setBackgroundResource(android.R.color.holo_red_dark)
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
//            minimumHeight = tabLayout.minimumHeight
            slidingLinearTabLayout = SlidingLinearTabLayout(tabLayout.context)
            addView(slidingLinearTabLayout.apply {
                //                minimumHeight = tabLayout.minimumHeight
                orientation = tabLayout.orientation
            })
        }

    override fun addView(view: View, layoutParams: ViewGroup.LayoutParams) {
        slidingLinearTabLayout.addView(view)
    }

    private inner class SlidingLinearTabLayout(context: Context) : LinearLayout(context) {
        init {
            setBackgroundResource(android.R.color.holo_blue_dark)
            setWillNotDraw(false)
            gravity = Gravity.CENTER
        }
    }
}