package com.liang.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.PointerIconCompat
import android.support.v4.view.ViewCompat
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.TooltipCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.liang.tablayout3x.ItemDecoration
import com.liang.tablayout3x.R
import com.liang.tablayout3x.Tab
import com.liang.utils.*

open class TabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr), Tab {


    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.TabItem,
            defStyleAttr, 0
        )
        title = typedArray.getText(R.styleable.TabItem_android_title)
        this.defaultIcon = typedArray.getDrawable(R.styleable.TabItem_android_icon)
        tabTitleSize =
            typedArray.getDimensionPixelSize(R.styleable.TabItem_android_textSize, 0).toFloat()
        tabIconTint = context.getColorStateList(
            typedArray,
            R.styleable.TabItem_android_icon
        )
        tabIconTintMode = parseTintMode(
            typedArray.getInt(R.styleable.TabItem_android_tintMode, -1),
            null
        )

        this.tabRippleColor = typedArray.getColor(
            R.styleable.TabItem_rippleColor, 0
        )
        this.tabUnboundedRipple = typedArray.getBoolean(R.styleable.TabItem_unboundedRipple, false)

        tabBackgroundResId =
            typedArray.getResourceId(R.styleable.TabItem_android_background, 0)
        if (typedArray.hasValue(R.styleable.TabItem_android_textColor)) {
            tabTitleColors = context.getColorStateList(
                typedArray,
                R.styleable.TabItem_android_textColor
            )
        }
        typedArray.recycle()
    }

    override val contentWidth: Int
        get() {
            var initialized = false
            var left = 0
            var right = 0
            val var4 = arrayOf(textView, iconView, badgeView)
            val var5 = var4.size

            for (var6 in 0 until var5) {
                val view = var4[var6]
                if (view != null && view.visibility == 0) {
                    left = if (initialized) left.coerceAtMost(view.left) else view.left
                    right = if (initialized) right.coerceAtLeast(view.right) else view.right
                    initialized = true
                }
            }

            return right - left
        }

    override var title: CharSequence? = null
        set(value) {
            field = value
            updateView()
        }

    override var defaultIcon: Drawable? = null
        set(value) {
            field = value
            updateView()
        }
    override var normalIcon: Drawable? = null
        set(value) {
            field = value
            updateView()
        }
    override var selectedIcon: Drawable? = null
        set(value) {
            field = value
            updateView()
        }

    override val view: View
        get() = this

    override var position: Int = -1

    override var tabLayout: TabLayout? = null

    override var tabTitleColors: ColorStateList? = null
        set(value) {
            field = value
            updateView()
        }
    override var tabTitleSize: Float = 0F
        set(value) {
            field = value
            updateView()
        }
    override var tabIconTint: ColorStateList? = null
        set(value) {
            field = value
            updateView()
        }

    override var tabIconTintMode: PorterDuff.Mode? = null
        set(value) {
            field = value
            updateView()
        }

    override var isTabTitleBold: Boolean = false
        set(value) {
            field = value
            updateView()
        }
    override var inlineLabel: Boolean = false
        set(value) {
            field = value
            initTabView()
        }

    override var tabUnboundedRipple: Boolean = false
        set(value) {
            field = value
            updateBackgroundDrawable()
        }
    override var tabBackgroundResId: Int = 0
        set(value) {
            field = value
            updateBackgroundDrawable()
        }
    override var tabDecoration: ItemDecoration? = null
        set(value) {
            field = value
            tabLayout?.invalidate()
        }

    override var tabRippleColor: Int = 0
        set(value) {
            field = value
            updateBackgroundDrawable()
        }


    override fun setTitle(title: String): Tab {
        this.title = title
        return this
    }

    override fun setTitleColor(normalColor: Int, selectedColor: Int): Tab {
        return setTitleColor(createColorStateList(normalColor, selectedColor))
    }

    override fun setTitleColor(textColor: ColorStateList?): Tab {
        this.tabTitleColors = textColor
        return this
    }

    override fun setTitleSize(sizePx: Float): Tab {
        this.tabTitleSize = sizePx
        return this
    }

    override fun setTitleBold(isBold: Boolean): Tab {
        this.isTabTitleBold = isBold
        return this
    }

    override fun setIcon(icon: Int): Tab {
        val drawable: Drawable? = ContextCompat.getDrawable(context, icon)
        return setIcon(drawable)
    }

    override fun setIcon(icon: Drawable?): Tab {
        this.defaultIcon = icon
        return this
    }

    override fun setIcon(normalIcon: Int, selectedIcon: Int): Tab {
        val normalDrawable: Drawable? = ContextCompat.getDrawable(this.context, normalIcon)
        val selectedDrawable: Drawable? = ContextCompat.getDrawable(this.context, selectedIcon)
        return setIcon(normalDrawable, selectedDrawable)
    }

    override fun setIcon(normalIcon: Drawable?, selectedIcon: Drawable?): Tab {
        this.normalIcon = normalIcon
        this.selectedIcon = selectedIcon
        return this
    }

    override fun setIconTint(normalColor: Int, selectedColor: Int): Tab {
        return setIconTint(createColorStateList(normalColor, selectedColor))
    }

    override fun setIconTint(tabIconTint: ColorStateList): Tab {
        this.tabIconTint = tabIconTint
        return this
    }

    override fun setRippleColor(tabRippleColor: Int): Tab {
        this.tabRippleColor = tabRippleColor
        return this
    }

    override fun setBackground(resId: Int): Tab {
        this.tabBackgroundResId = resId
        return this
    }

    override fun setUnboundedRipple(unboundedRipple: Boolean): Tab {
        this.tabUnboundedRipple = unboundedRipple
        return this
    }

    override fun setDecoration(itemDecoration: ItemDecoration): Tab {
        this.tabDecoration = itemDecoration
        return this
    }

    override fun setTabPadding(
        tabPaddingStart: Int,
        tabPaddingTop: Int,
        tabPaddingEnd: Int,
        tabPaddingBottom: Int
    ): Tab {
        ViewCompat.setPaddingRelative(
            this,
            tabPaddingStart,
            tabPaddingTop,
            tabPaddingEnd,
            tabPaddingBottom
        )
        return this
    }

    private lateinit var tabView: View
    private var textView: TextView? = null
    private var iconView: ImageView? = null
    private var badgeView: BadgeView? = null

    private val params = LayoutParams(
        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
    )

    override fun select() {
        requireNotNull(tabLayout) { "Tab not attached to a TabLayout" }
        tabLayout?.selectTab(this)
    }

    override fun reset() {
        tabLayout = null
        tag = null
        defaultIcon = null
        normalIcon = null
        selectedIcon = null
        title = null
        position = -1
        tabTitleColors = null
        tabIconTintMode = null
        tabIconTint = null
        tabTitleSize = 0f
        tabRippleColor = 0
        tabBackgroundResId = 0
        this.isSelected = false
    }

    override fun showBadge(msg: String?) {
        badgeView?.show(msg)
    }

    override fun hideBadge() {
        badgeView?.hide()
    }

    override fun setBadgeTextColor(color: Int) {
        badgeView?.setBadgeTextColor(color)
    }

    override fun setBadgeTextSize(sp: Float) {
        badgeView?.setBadgeTextSize(sp)
    }

    override fun setBadgeBackgroundColor(color: Int) {
        badgeView?.setBadgeBackgroundColor(color)
    }

    override fun setBadgeStroke(width: Int, color: Int) {
        badgeView?.setBadgeStroke(width, color)
    }

    override fun updateColor(offset: Float) {
//        Log.e("Item", "Item: ${position},updateColor: $offset")
        tabTitleColors?.let {
            textView?.setTextColor(
                getColorFrom(
                    it.getColorForState(
                        View.EMPTY_STATE_SET,
                        Color.GRAY
                    ), it.getColorForState(
                        View.SELECTED_STATE_SET,
                        Color.GRAY
                    ), offset
                )
            )
        }
    }

    override fun updateScale(scale: Float) {
        scaleX = scale
        scaleY = scale
    }

    init {
        this.isClickable = true
        initTabView()
        ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(context, 1002))
    }

    private fun initTabView() {
        removeAllViews()
        tabView = setContentView()
        iconView = setTabIconView()
        textView = setTabTitleView()
        badgeView = setTabBadgeView()
        params.gravity = Gravity.CENTER
        addView(tabView, params)
        updateBackgroundDrawable()
        updateView()
    }

    protected open fun setContentView(): View = LayoutInflater.from(context).inflate(
        if (inlineLabel) R.layout.tab_item_horizontal else R.layout.tab_item_vertical, null
    )

    protected open fun setTabTitleView(): TextView? = tabView.findViewById(R.id.tab_title)

    protected open fun setTabIconView(): ImageView? = tabView.findViewById(R.id.tab_icon)

    protected open fun setTabBadgeView(): BadgeView? = tabView.findViewById(R.id.tab_badgeView)

    override fun performClick(): Boolean {
        val handled = super.performClick()
        return if (tabLayout != null) {
            if (!handled) {
                playSoundEffect(0)
            }
            select()
            true
        } else {
            handled
        }
    }

    private fun updateView() {
        updateTextAndIcon(textView, iconView)
        if (!TextUtils.isEmpty(contentDescription)) {
            this.contentDescription = contentDescription
        }
        this.isSelected = isSelected
    }

    private fun updateTextAndIcon(textView: TextView?, iconView: ImageView?) {
        iconView?.let { iv ->
            val icon =
                if (defaultIcon != null) DrawableCompat.wrap(defaultIcon!!).mutate() else null
            if (normalIcon == null && selectedIcon == null && icon == null) {
                iv.visibility = GONE
                iv.setImageDrawable(null)
            } else if (normalIcon != null || selectedIcon != null) {
                iv.visibility = VISIBLE
                iv.setImageDrawable(
                    if (iv.isSelected) {
                        selectedIcon ?: normalIcon
                    } else {
                        normalIcon
                    }
                )
                this.visibility = VISIBLE
            } else {
                iv.visibility = VISIBLE
                icon?.let { ic ->
                    tabIconTint?.let {
                        DrawableCompat.setTintList(ic, it)
                    }
                    tabIconTintMode?.let {
                        DrawableCompat.setTintMode(ic, it)
                    }
                }
                iv.setImageDrawable(icon)
            }
        }

        val hasText = !title.isNullOrEmpty()

        textView?.let { tv ->
            if (hasText) {
                tv.text = title
                tv.visibility = VISIBLE
                this.visibility = VISIBLE
            } else {
                tv.visibility = GONE
                tv.text = null
            }

            tabTitleColors?.let {
                tv.setTextColor(it)
            }

            if (tabTitleSize > 0f) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTitleSize)
            }

            if (isTabTitleBold) {
                tv.typeface =
                    Typeface.defaultFromStyle(if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            }
        }

        val contentDesc = contentDescription
        TooltipCompat.setTooltipText(this, if (hasText) null else contentDesc)
    }

    override fun setSelected(selected: Boolean) {
        val changed = this.isSelected != selected
        super.setSelected(selected)
        if (changed && selected && Build.VERSION.SDK_INT < 16) {
            sendAccessibilityEvent(4)
        }

        iconView?.let { iv ->
            val icon =
                if (defaultIcon != null) DrawableCompat.wrap(defaultIcon!!).mutate() else null
            iv.isSelected = selected
            if (normalIcon == null && selectedIcon == null && icon == null) {
                iv.visibility = GONE
            } else {
                iv.visibility = VISIBLE
                if (normalIcon != null || selectedIcon != null) {
                    iv.setImageDrawable(
                        if (iv.isSelected) {
                            selectedIcon ?: normalIcon
                        } else {
                            normalIcon
                        }
                    )
                }
            }
        }

        this.textView?.isSelected = selected
        if (isTabTitleBold) {
            textView?.typeface =
                Typeface.defaultFromStyle(if (isSelected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private var baseBackgroundDrawable: Drawable? = null

    private fun updateBackgroundDrawable() {
        if (tabBackgroundResId != 0) {
            this.baseBackgroundDrawable =
                AppCompatResources.getDrawable(context, tabBackgroundResId)
            if (this.baseBackgroundDrawable != null && this.baseBackgroundDrawable?.isStateful!!) {
                this.baseBackgroundDrawable?.state = this.drawableState
            }
        } else {
            this.baseBackgroundDrawable = null
        }
        val contentDrawable: Drawable = GradientDrawable()
        (contentDrawable as GradientDrawable).setColor(0)
        val background: Any
        val tabRippleColorStateList = createColorStateList(tabRippleColor, tabRippleColor)
        val maskDrawable = GradientDrawable()
        maskDrawable.cornerRadius = 1.0E-5f
        maskDrawable.setColor(-1)
        val rippleColor = RippleUtils.convertToRippleDrawableColor(tabRippleColorStateList)
        background = if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable(
                rippleColor,
                if (this.tabUnboundedRipple) null else contentDrawable,
                if (this.tabUnboundedRipple) null else maskDrawable
            )
        } else {
            val rippleDrawable = DrawableCompat.wrap(maskDrawable)
            DrawableCompat.setTintList(rippleDrawable, rippleColor)
            LayerDrawable(arrayOf(contentDrawable, rippleDrawable))
        }
        ViewCompat.setBackground(this, background as Drawable)
        tabLayout?.invalidate()
    }

    override fun drawBackground(canvas: Canvas) {
        if (this.baseBackgroundDrawable != null) {
            this.baseBackgroundDrawable?.setBounds(
                this.left,
                this.top,
                this.right,
                this.bottom
            )
            this.baseBackgroundDrawable?.draw(canvas)
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        var changed = false
        val state = this.drawableState
        if (this.baseBackgroundDrawable != null && this.baseBackgroundDrawable?.isStateful!!) {
            changed = changed or this.baseBackgroundDrawable?.setState(state)!!
        }
        if (changed) {
            this.invalidate()
            tabLayout?.invalidate()
        }
    }


    class DefItemDecoration @JvmOverloads constructor(
        width: Int = 0,
        height: Int = 0
    ) :
        ItemDecoration(width, height) {

        var color: Int = 0
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                paint.color = color
            }

        init {
            Log.e("DefItemDecoration", "width: $width")
            paint.strokeWidth = width.toFloat()
        }

        override fun getItemOffsets(outRect: Rect) {
            outRect.set(0, 0, width, height)
        }

        override fun onDraw(canvas: Canvas, rect: Rect) {
            canvas.drawRect(rect, paint)
        }
    }

    init {
        initAttrs(context, attrs, defStyleAttr)
    }

}