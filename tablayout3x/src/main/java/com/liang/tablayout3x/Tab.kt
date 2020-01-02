package com.liang.tablayout3x

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.view.View
import com.liang.widget.TabLayout


interface Tab {
    val contentWidth: Int

    var title: CharSequence?
    var tabTitleColors: ColorStateList?

    var defaultIcon: Drawable?
    var normalIcon: Drawable?
    var selectedIcon: Drawable?
    var tabIconTint: ColorStateList?

    val view: View
    var position: Int
    var tabLayout: TabLayout?

    var tabTitleSize: Float

    var tabRippleColor: Int
    var tabIconTintMode: PorterDuff.Mode?
    var tabUnboundedRipple: Boolean
    var isTabTitleBold: Boolean
    var inlineLabel: Boolean
    var tabBackgroundResId: Int
    var tabDecoration: ItemDecoration?


    fun setTitle(title: String): Tab

    fun setTitleColor(@ColorInt normalColor: Int, @ColorInt selectedColor: Int): Tab

    fun setTitleColor(textColor: ColorStateList?): Tab

    fun setTitleSize(sizePx: Float): Tab

    fun setTitleBold(isBold: Boolean): Tab

    fun setIcon(@DrawableRes icon: Int): Tab

    fun setIcon(icon: Drawable?): Tab

    fun setIconTint(@ColorInt normalColor: Int, @ColorInt selectedColor: Int): Tab

    fun setIconTint(tabIconTint: ColorStateList): Tab

    fun setIcon(@DrawableRes normalIcon: Int, @DrawableRes selectedIcon: Int): Tab

    fun setIcon(normalIcon: Drawable?, selectedIcon: Drawable?): Tab

    fun setRippleColor(tabRippleColor: Int): Tab

    fun setBackground(@DrawableRes resId: Int): Tab

    fun setUnboundedRipple(unboundedRipple: Boolean): Tab

    fun setDecoration(itemDecoration: ItemDecoration): Tab

    fun setTabPadding(
        tabPaddingStart: Int,
        tabPaddingTop: Int,
        tabPaddingEnd: Int,
        tabPaddingBottom: Int
    ): Tab

    fun select()

    fun isSelected(): Boolean

    fun reset()

    fun showBadge(msg: String?)

    fun hideBadge()

    fun setBadgeTextColor(color: Int)

    fun setBadgeTextSize(sp: Float)

    fun setBadgeBackgroundColor(color: Int)

    fun drawBackground(canvas: Canvas)

    fun setBadgeStroke(width: Int, color: Int)

    fun updateColor(offset: Float)

    fun updateScale(scale: Float)
}


interface Badge {
    fun show(msg: String?)

    fun hide()

    fun setBadgeTextSize(sp: Float)

    fun setBadgeBackgroundColor(color: Int)

    fun setBadgeStroke(width: Int, color: Int)

    fun setBadgeTextColor(color: Int)
}


abstract class ItemDecoration(width: Int = 0, height: Int = 0) {

    private val outRect: Rect = Rect()

    var width: Int = width
        get() = if (field == 0) outRect.width() else field
    var height: Int = height
        get() = if (field == 0) outRect.height() else field

    protected val paint: Paint = Paint()

    init {
        paint.isAntiAlias = true
        getItemOffsets(outRect)
    }

    open fun onDraw(canvas: Canvas, rect: Rect) {
    }

    open fun getItemOffsets(outRect: Rect) {
    }
}


interface TabAdapter {
    fun getTab(position: Int): Tab
}




