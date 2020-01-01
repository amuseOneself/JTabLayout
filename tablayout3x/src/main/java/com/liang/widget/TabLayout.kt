package com.liang.widget

import android.content.Context
import android.support.v4.util.Pools
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.liang.tablayout3x.FlowLayoutManager
import com.liang.tablayout3x.GridLayoutManager
import com.liang.tablayout3x.LinearLayoutManager
import com.liang.tablayout3x.R

/**
 * TODO: document your custom view class.
 */
class TabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    companion object {
        const val Horizontal = 0
        const val Vertical = 1
        const val Linear = 0
        const val Grid = 1
        const val Flow = 2
    }

    private val tabPool: Pools.Pool<Tab> = Pools.SynchronizedPool(16)
    var orientation: Int = Horizontal
    var tabLayoutMode = Linear

    private lateinit var layoutManager: LayoutManager

    init {
        initAttrs(attrs, defStyleAttr)
        initTabLayoutManager()
    }


    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyleAttr, 0)

        orientation = typedArray.getInt(R.styleable.TabLayout_android_orientation, Horizontal)
        tabLayoutMode = typedArray.getInt(R.styleable.TabLayout_layoutMode, Linear)

        typedArray.recycle()

        setBackgroundResource(android.R.color.holo_green_dark)
    }

    private fun initTabLayoutManager() {
        layoutManager = when (tabLayoutMode) {
            Grid -> GridLayoutManager(this)
            Flow -> FlowLayoutManager(this)
            else -> LinearLayoutManager(this)
        }

        repeat(100) {
            layoutManager.addView(TextView(context).apply {
                text = "addView: $it"
                gravity = Gravity.CENTER
                setBackgroundResource(android.R.color.holo_orange_dark)
            }, LayoutParams(300,200))
        }
    }

    class Tab{}

    abstract class LayoutManager(protected val tabLayout: TabLayout) {
        init {
            tabLayout.removeAllViews()
            tabLayout.addView(getSlidingLayout())
        }

        abstract fun getTabLayoutParams(): ViewGroup.LayoutParams

        protected abstract fun getSlidingLayout(): View

        abstract fun addView(view: View, layoutParams: ViewGroup.LayoutParams)

    }

}
