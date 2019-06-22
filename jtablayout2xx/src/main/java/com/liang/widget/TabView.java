package com.liang.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.jtablayout.badge.Badge;
import com.liang.jtablayout.tab.Tab;
import com.liang.jtablayout.tab.TabChild;
import com.liang.jtablayout.ripple.RippleUtils;
import com.liang.jtablayout.utils.ColorUtils;
import com.liang.jtablayoutx.R;


public class TabView extends FrameLayout implements TabChild {

    private Tab tab;
    private TextView textView;
    private ImageView iconView;
    private View badgeView;

    @Nullable
    private Drawable baseBackgroundDrawable;
    private View tabView;

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

    private void updateLayout() {
        removeAllViews();

        tabView = setContentView();
        if (tabView == null) {
            tabView = LayoutInflater.from(getContext()).inflate(
                    tab.getInline() ? R.layout.tab_item_horizontal : R.layout.tab_item_vertical, null, true);
        }
        iconView = setTabIconView();
        textView = setTabTitleView();
        badgeView = setTabBadgeView();

        addView(tabView);
        updateBackgroundDrawable(getContext());
        update();
    }

    private View setActiveView() {
        return tabView.findViewById(R.id.tab);
    }

    protected View setContentView() {
        return null;
    }

    protected TextView setTabTitleView() {
        return tabView.findViewById(R.id.tab_title);
    }

    protected ImageView setTabIconView() {
        return tabView.findViewById(R.id.tab_icon);
    }

    @Override
    public void updateBackgroundDrawable(Context context) {
        if (tab.getTabBackgroundResId() != 0) {
            this.baseBackgroundDrawable = AppCompatResources.getDrawable(context, tab.getTabBackgroundResId());
            if (this.baseBackgroundDrawable != null && this.baseBackgroundDrawable.isStateful()) {
                this.baseBackgroundDrawable.setState(this.getDrawableState());
            }
        } else {
            this.baseBackgroundDrawable = null;
        }

        Drawable contentDrawable = new GradientDrawable();
        ((GradientDrawable) contentDrawable).setColor(0);
        Object background;
        if (tab.getTabRippleColorStateList() != null) {
            GradientDrawable maskDrawable = new GradientDrawable();
            maskDrawable.setCornerRadius(1.0E-5F);
            maskDrawable.setColor(-1);
            ColorStateList rippleColor = RippleUtils.convertToRippleDrawableColor(tab.getTabRippleColorStateList());
            if (Build.VERSION.SDK_INT >= 21) {
                background = new RippleDrawable(rippleColor, tab.isUnboundedRipple() ? null : contentDrawable, tab.isUnboundedRipple() ? null : maskDrawable);
            } else {
                Drawable rippleDrawable = DrawableCompat.wrap(maskDrawable);
                DrawableCompat.setTintList(rippleDrawable, rippleColor);
                background = new LayerDrawable(new Drawable[]{contentDrawable, rippleDrawable});
            }
        } else {
            background = contentDrawable;
        }

        ViewCompat.setBackground(this, (Drawable) background);
        tab.getParent().postInvalidate();
    }

    protected BadgeView setTabBadgeView() {
        return tabView.findViewById(R.id.tab_badgeView);
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
            tab.getParent().invalidate();
        }
    }

    @Override
    public boolean performClick() {
        boolean handled = super.performClick();
        if (this.tab != null) {
            if (!handled) {
                this.playSoundEffect(0);
            }

            this.tab.select();
            return true;
        } else {
            return handled;
        }
    }

    @Override
    public void setSelected(boolean selected) {
        boolean changed = this.isSelected() != selected;
        super.setSelected(selected);
        if (changed && selected && Build.VERSION.SDK_INT < 16) {
            this.sendAccessibilityEvent(4);
        }


        if (this.iconView != null) {
            this.iconView.setSelected(selected);
            Drawable normalIcon = tab != null ? tab.getNormalIcon() : null;
            Drawable selectedIcon = tab != null ? tab.getSelectedIcon() : null;
            if (normalIcon == null && selectedIcon == null) {
                this.iconView.setVisibility(GONE);
            } else {
                this.iconView.setVisibility(VISIBLE);
                this.iconView.setImageDrawable(selected ? selectedIcon != null ? selectedIcon : normalIcon : normalIcon != null ? normalIcon : selectedIcon);
            }
        }

        if (this.textView != null) {
            this.textView.setSelected(selected);
            if (tab.isTabTextBold()) {
                textView.setTypeface(Typeface.defaultFromStyle(isSelected() ? Typeface.BOLD : Typeface.NORMAL));
            }
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(android.support.v7.app.ActionBar.Tab.class.getName());
    }

    @TargetApi(14)
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(android.support.v7.app.ActionBar.Tab.class.getName());
    }

    @Override
    public void setTab(@Nullable Tab tab) {
        if (tab != null) {
            this.tab = tab;
            updateLayout();
        }
    }

    @Override
    public void reset() {
        this.setTab(null);
        this.setSelected(false);
    }

    @Override
    public void update() {
        Tab tab = this.tab;

        if (tab == null) {
            throw new NullPointerException("Tab is null");
        }

        ViewCompat.setPaddingRelative(this, tab.getTabPaddingStart(), tab.getTabPaddingTop(), tab.getTabPaddingEnd(), tab.getTabPaddingBottom());

//        TextViewCompat.setTextAppearance(this.textView, tab.parent.tabTextAppearance);
        if (textView != null) {
            if (tab.getTextColor() != null) {
                this.textView.setTextColor(tab.getTextColor());
            }

            if (tab.getTabTextSize() > 0) {
                this.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tab.getTabTextSize());
            }
        }

        updateTextAndIcon(this.textView, this.iconView);

        if (!TextUtils.isEmpty(tab.getContentDesc())) {
            this.setContentDescription(tab.getContentDesc());
        }

        this.setSelected(tab.isSelected());
    }

    @Override
    public void updateOrientation(boolean inline) {
        this.updateLayout();
    }

    @Override
    public View getView() {
        return this;
    }

    private void updateTextAndIcon(@Nullable TextView textView, @Nullable ImageView iconView) {
        if (textView == null && iconView == null) {
            return;
        }

        Drawable normalIcon = tab != null ? tab.getNormalIcon() : null;
        Drawable selectedIcon = tab != null ? tab.getSelectedIcon() : null;
        Drawable icon = tab != null && tab.getIcon() != null ? DrawableCompat.wrap(tab.getIcon()).mutate() : null;

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
                DrawableCompat.setTintList(icon, tab.getTabIconTint());
                if (tab.getTabIconTintMode() != null) {
                    DrawableCompat.setTintMode(icon, tab.getTabIconTintMode());
                }
            }
        }

        CharSequence text = this.tab != null ? this.tab.getText() : null;
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
            if (tab.isTabTextBold()) {
                textView.setTypeface(Typeface.defaultFromStyle(isSelected() ? Typeface.BOLD : Typeface.NORMAL));
            }
        }

        CharSequence contentDesc = this.tab != null ? this.tab.getContentDesc() : null;
        TooltipCompat.setTooltipText(this, hasText ? null : contentDesc);
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
        textView.setTextColor(ColorUtils.getColorFrom(tab.getTextColor().getColorForState(EMPTY_STATE_SET, Color.GRAY),
                tab.getTextColor().getColorForState(SELECTED_STATE_SET, Color.GRAY), offset));
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

    public Tab getTab() {
        return this.tab;
    }
}
