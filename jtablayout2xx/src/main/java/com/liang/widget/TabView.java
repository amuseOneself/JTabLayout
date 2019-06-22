package com.liang.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.jtablayout.badge.Badge;
import com.liang.jtablayout.ripple.RippleUtils;
import com.liang.jtablayout.utils.ColorUtils;
import com.liang.jtablayoutx.R;
import com.liang.jtablayout.tab.Tab;

public class TabView extends FrameLayout implements Tab {

    private JTabLayout parent;
    private Drawable icon;
    private Drawable normalIcon;
    private Drawable selectedIcon;
    private CharSequence title;
    private int position = -1;

    private boolean inline;
    private ColorStateList titleColor;
    private android.graphics.PorterDuff.Mode tabIconTintMode;
    private ColorStateList tabIconTint;
    private float tabTitleSize = 0;
    private int tabBackgroundResId;
    private ColorStateList tabRippleColorStateList;
    private boolean unboundedRipple;

    private int tabPaddingStart;
    private int tabPaddingTop;
    private int tabPaddingEnd;
    private int tabPaddingBottom;

    private boolean tabTextBold;

    private Object object;

    private View tabView;
    private TextView textView;
    private ImageView iconView;
    private View badgeView;
    private FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    private Drawable baseBackgroundDrawable;

    public TabView(@NonNull Context context) {
        this(context, null);
    }

    public TabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setFocusable(true);
        this.setClickable(true);
    }


    @Override
    public void initTabView() {
        removeAllViews();
        tabView = setContentView();
        iconView = setTabIconView();
        textView = setTabTitleView();
        badgeView = setTabBadgeView();
        params.gravity = Gravity.CENTER;
        addView(tabView, params);
        updateBackgroundDrawable();
        updateView();
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public Tab setObject(Object object) {
        this.object = object;
        return this;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public Tab setPosition(int position) {
        this.position = position;
        initTabView();
        return this;
    }

    @Override
    public boolean getInline() {
        return inline;
    }

    @Override
    public Tab setInlineLabel(boolean inline) {
        this.inline = inline;
        initTabView();
        return this;
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    @Override
    public Tab setTitle(CharSequence text) {
        this.title = text;
        updateView();
        return this;
    }

    @Override
    public Tab setTitle(@StringRes int resId) {
        return setTitle(getContext().getString(resId));
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public Drawable getNormalIcon() {
        return normalIcon;
    }

    @Override
    public Drawable getSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public Tab setIcon(int icon) {
        Drawable drawable = ContextCompat.getDrawable(this.getContext(), icon);
        return setIcon(drawable);
    }

    @Override
    public Tab setIcon(Drawable icon) {
        this.icon = icon;
        updateView();
        return this;
    }

    @Override
    public Tab setIcon(int normalIcon, int selectedIcon) {
        Drawable normalDrawable = ContextCompat.getDrawable(this.getContext(), normalIcon);
        Drawable selectedDrawable = ContextCompat.getDrawable(this.getContext(), selectedIcon);
        return setIcon(normalDrawable, selectedDrawable);
    }

    @Override
    public Tab setIcon(Drawable normalIcon, Drawable selectedIcon) {
        this.normalIcon = normalIcon;
        this.selectedIcon = selectedIcon;
        updateView();
        return this;
    }

    @Override
    public ColorStateList getTitleColor() {
        return titleColor;
    }

    @Override
    public Tab setTitleColor(int defaultColor, int selectedColor) {
        return setTitleColor(ColorUtils.createColorStateList(defaultColor, selectedColor));
    }

    @Override
    public Tab setTitleColor(ColorStateList textColor) {
        this.titleColor = textColor;
        updateView();
        return this;
    }

    @Override
    public ColorStateList getTabIconTint() {
        return tabIconTint;
    }

    @Override
    public Tab setTabIconTint(int defaultColor, int selectedColor) {
        return setTabIconTint(ColorUtils.createColorStateList(defaultColor, selectedColor));
    }

    @Override
    public Tab setTabIconTint(ColorStateList tabIconTint) {
        this.tabIconTint = tabIconTint;
        updateView();
        return this;
    }

    @Override
    public PorterDuff.Mode getTabIconTintMode() {
        return tabIconTintMode;
    }

    @Override
    public Tab setTabIconTintMode(PorterDuff.Mode tabIconTintMode) {
        this.tabIconTintMode = tabIconTintMode;
        updateView();
        return this;
    }

    @Override
    public int getTabBackgroundResId() {
        return tabBackgroundResId;
    }

    @Override
    public Tab setTabBackgroundResId(int resId) {
        this.tabBackgroundResId = resId;
        updateBackgroundDrawable();
        return this;
    }

    @Override
    public float getTabTextSize() {
        return tabTitleSize;
    }

    @Override
    public Tab setTabTextSize(float sizePx) {
        this.tabTitleSize = sizePx;
        return this;
    }

    @Override
    public boolean isTabTextBold() {
        return tabTextBold;
    }

    @Override
    public Tab setTabTextBold(boolean isBold) {
        this.tabTextBold = isBold;
        return this;
    }

    @Override
    public void setContentDescription(int resId) {
        setContentDescription(getContext().getString(resId));
    }

    @Override
    public ColorStateList getTabRippleColorStateList() {
        return tabRippleColorStateList;
    }

    @Override
    public Tab setTabRippleColorStateList(ColorStateList tabRippleColorStateList) {
        this.tabRippleColorStateList = tabRippleColorStateList;
        updateBackgroundDrawable();
        return this;
    }

    @Override
    public boolean isUnboundedRipple() {
        return unboundedRipple;
    }

    @Override
    public Tab setUnboundedRipple(boolean unboundedRipple) {
        this.unboundedRipple = unboundedRipple;
        updateBackgroundDrawable();
        return null;
    }

    @Override
    public Tab setTabPadding(int tabPaddingStart, int tabPaddingTop, int tabPaddingEnd, int tabPaddingBottom) {
        this.tabPaddingStart = tabPaddingStart;
        this.tabPaddingTop = tabPaddingTop;
        this.tabPaddingEnd = tabPaddingEnd;
        this.tabPaddingBottom = tabPaddingBottom;
        return this;
    }

    @Override
    public int getTabPaddingStart() {
        return tabPaddingStart;
    }

    @Override
    public int getTabPaddingTop() {
        return tabPaddingTop;
    }

    @Override
    public int getTabPaddingEnd() {
        return tabPaddingEnd;
    }

    @Override
    public int getTabPaddingBottom() {
        return tabPaddingBottom;
    }

    @Override
    public JTabLayout getTabLayout() {
        return parent;
    }

    @Override
    public Tab setTabLayout(JTabLayout tabLayout) {
        this.parent = tabLayout;
        return this;
    }

    @Override
    public void select() {
        if (this.parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        } else {
            this.parent.selectTab(this);
        }
    }

    @Override
    public void updateView() {

        ViewCompat.setPaddingRelative(this, getTabPaddingStart(), getTabPaddingTop(), getTabPaddingEnd(), getTabPaddingBottom());

//        TextViewCompat.setTextAppearance(this.textView, tab.parent.tabTextAppearance);
        if (textView != null) {
            if (getTitleColor() != null) {
                this.textView.setTextColor(getTitleColor());
            }

            if (getTabTextSize() > 0) {
                this.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTabTextSize());
            }
        }

        updateTextAndIcon(this.textView, this.iconView);

        if (!TextUtils.isEmpty(getContentDescription())) {
            this.setContentDescription(getContentDescription());
        }

        this.setSelected(isSelected());
    }

    private void updateTextAndIcon(TextView textView, ImageView iconView) {
        if (textView == null && iconView == null) {
            return;
        }

        Drawable normalIcon = getNormalIcon();
        Drawable selectedIcon = getSelectedIcon();
        Drawable icon = getIcon() != null ? DrawableCompat.wrap(getIcon()).mutate() : null;

        if (iconView != null) {
            if (normalIcon == null && selectedIcon == null && icon == null) {
                iconView.setVisibility(GONE);
                iconView.setImageDrawable(null);
            } else if (normalIcon != null || selectedIcon != null) {
                iconView.setVisibility(VISIBLE);
                iconView.setImageDrawable(iconView.isSelected() ? selectedIcon != null ? selectedIcon : normalIcon : normalIcon != null ? normalIcon : selectedIcon);
                this.setVisibility(VISIBLE);
            } else {
                iconView.setVisibility(VISIBLE);
                DrawableCompat.setTintList(icon, getTabIconTint());
                if (getTabIconTintMode() != null) {
                    DrawableCompat.setTintMode(icon, getTabIconTintMode());
                }
            }
        }

        CharSequence text = getTitle();
        boolean hasText = !TextUtils.isEmpty(text);

        if (textView != null) {
            if (hasText) {
                textView.setText(text);
                textView.setVisibility(VISIBLE);
                this.setVisibility(VISIBLE);
            } else {
                textView.setVisibility(GONE);
                textView.setText(null);
            }
            if (isTabTextBold()) {
                textView.setTypeface(Typeface.defaultFromStyle(isSelected() ? Typeface.BOLD : Typeface.NORMAL));
            }
        }

        CharSequence contentDesc = getContentDescription();
        TooltipCompat.setTooltipText(this, hasText ? null : contentDesc);
    }

    protected View setContentView() {
        return LayoutInflater.from(getContext()).inflate(
                getInline() ? R.layout.tab_item_horizontal : R.layout.tab_item_vertical, null);
    }

    protected TextView setTabTitleView() {
        return tabView.findViewById(R.id.tab_title);
    }

    protected ImageView setTabIconView() {
        return tabView.findViewById(R.id.tab_icon);
    }

    protected BadgeView setTabBadgeView() {
        return tabView.findViewById(R.id.tab_badgeView);
    }

    @Override
    public void updateBackgroundDrawable() {
        if (getTabBackgroundResId() != 0) {
            this.baseBackgroundDrawable = AppCompatResources.getDrawable(getContext(), getTabBackgroundResId());
            if (this.baseBackgroundDrawable != null && this.baseBackgroundDrawable.isStateful()) {
                this.baseBackgroundDrawable.setState(this.getDrawableState());
            }
        } else {
            this.baseBackgroundDrawable = null;
        }

        Drawable contentDrawable = new GradientDrawable();
        ((GradientDrawable) contentDrawable).setColor(0);
        Object background;
        if (getTabRippleColorStateList() != null) {
            GradientDrawable maskDrawable = new GradientDrawable();
            maskDrawable.setCornerRadius(1.0E-5F);
            maskDrawable.setColor(-1);
            ColorStateList rippleColor = RippleUtils.convertToRippleDrawableColor(getTabRippleColorStateList());
            if (Build.VERSION.SDK_INT >= 21) {
                background = new RippleDrawable(rippleColor, isUnboundedRipple() ? null : contentDrawable, isUnboundedRipple() ? null : maskDrawable);
            } else {
                Drawable rippleDrawable = DrawableCompat.wrap(maskDrawable);
                DrawableCompat.setTintList(rippleDrawable, rippleColor);
                background = new LayerDrawable(new Drawable[]{contentDrawable, rippleDrawable});
            }
        } else {
            background = contentDrawable;
        }

        ViewCompat.setBackground(this, (Drawable) background);
        if (getTabLayout() != null) {
            getTabLayout().postInvalidate();
        }
    }

    @Override
    public void drawBackground(Canvas canvas) {
        if (this.baseBackgroundDrawable != null) {
            this.baseBackgroundDrawable.setBounds(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
            this.baseBackgroundDrawable.draw(canvas);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        boolean changed = false;
        int[] state = this.getDrawableState();
        if (this.baseBackgroundDrawable != null && this.baseBackgroundDrawable.isStateful()) {
            changed |= this.baseBackgroundDrawable.setState(state);
        }

        if (changed) {
            this.invalidate();
            if (getTabLayout() != null) {
                getTabLayout().invalidate();
            }
        }
    }

    @Override
    public boolean performClick() {
        boolean handled = super.performClick();
        if (!handled) {
            this.playSoundEffect(0);
        }

        this.select();
        return true;
    }

    @Override
    public void setSelected(boolean selected) {
        boolean changed = this.isSelected() != selected;
        super.setSelected(selected);
        if (changed && selected && Build.VERSION.SDK_INT < 16) {
            this.sendAccessibilityEvent(4);
        }

        if (this.iconView != null) {
            Drawable normalIcon = getNormalIcon();
            Drawable selectedIcon = getSelectedIcon();
            Drawable icon = getIcon() != null ? DrawableCompat.wrap(getIcon()).mutate() : null;
            this.iconView.setSelected(selected);
            if (normalIcon == null && selectedIcon == null && icon == null) {
                this.iconView.setVisibility(GONE);
            } else {
                this.iconView.setVisibility(VISIBLE);
                if (normalIcon != null || selectedIcon != null) {
                    this.iconView.setImageDrawable(selected ? selectedIcon != null ? selectedIcon : normalIcon : normalIcon != null ? normalIcon : selectedIcon);
                }
            }
        }

        if (this.textView != null) {
            this.textView.setSelected(selected);
            if (isTabTextBold()) {
                textView.setTypeface(Typeface.defaultFromStyle(isSelected() ? Typeface.BOLD : Typeface.NORMAL));
            }
        }
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public int getContentWidth() {
        boolean initialized = false;
        int left = 0;
        int right = 0;
        View[] var4 = new View[]{this.textView, this.iconView};
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            View view = var4[var6];
            if (view != null && view.getVisibility() == VISIBLE) {
                left = initialized ? Math.min(left, view.getLeft()) : view.getLeft();
                right = initialized ? Math.max(right, view.getRight()) : view.getRight();
                initialized = true;
            }
        }

        return right - left;
    }

    @Override
    public void showBadge(String msg) {
        if (badgeView != null && badgeView instanceof BadgeView) {
            ((BadgeView) badgeView).show(msg);
        }
    }

    @Override
    public void hideBadge() {
        if (badgeView != null && badgeView instanceof BadgeView) {
            ((BadgeView) badgeView).hide();
        }
    }

    @Override
    public void updateColor(float offset) {
        textView.setTextColor(ColorUtils.getColorFrom(getTitleColor().getColorForState(EMPTY_STATE_SET, Color.GRAY),
                getTitleColor().getColorForState(SELECTED_STATE_SET, Color.GRAY), offset));
    }

    @Override
    public void updateScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    @Override
    public void setBadgeTextColor(int color) {
        if (badgeView != null && badgeView instanceof Badge) {
            ((Badge) badgeView).setBadgeTextColor(color);
        }
    }

    @Override
    public void setBadgeTextSize(float sp) {
        if (badgeView != null && badgeView instanceof Badge) {
            ((Badge) badgeView).setBadgeTextSize(sp);
        }
    }

    @Override
    public void setBadgeBackgroundColor(int color) {
        if (badgeView != null && badgeView instanceof Badge) {
            ((Badge) badgeView).setBadgeBackgroundColor(color);
        }
    }

    @Override
    public void setBadgeStroke(int width, int color) {
        if (badgeView != null && badgeView instanceof Badge) {
            ((Badge) badgeView).setBadgeStroke(width, color);
        }
    }

    @Override
    public void reset() {
        this.parent = null;
        this.object = null;
        setTag(null);
        this.icon = null;
        this.normalIcon = null;
        this.selectedIcon = null;
        this.title = null;
        this.position = -1;
        this.titleColor = null;
        this.tabIconTintMode = null;
        this.tabIconTint = null;
        this.tabTitleSize = 0;
        this.tabRippleColorStateList = null;
        this.tabBackgroundResId = 0;
        this.tabPaddingStart = 0;
        this.tabPaddingTop = 0;
        this.tabPaddingEnd = 0;
        this.tabPaddingBottom = 0;
        this.setSelected(false);
        this.updateScale(1.0f);
    }
}
