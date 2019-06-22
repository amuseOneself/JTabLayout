package com.liang.jtablayout.tab;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.liang.widget.JTabLayout;
import com.liang.jtablayout.utils.ColorUtils;

public class Tab<T extends TabChild> {
    private Object tag;
    private Drawable icon;
    private Drawable normalIcon;
    private Drawable selectedIcon;
    private CharSequence text;
    private CharSequence contentDesc;
    private int position = -1;
    private JTabLayout parent;
    private T tabItem;
    private boolean inline;
    private ColorStateList textColor;
    private android.graphics.PorterDuff.Mode tabIconTintMode;
    private ColorStateList tabIconTint;
    private float tabTextSize = 0;
    private int tabBackgroundResId;
    private ColorStateList tabRippleColorStateList;
    private boolean unboundedRipple;

    private int tabPaddingStart;
    private int tabPaddingTop;
    private int tabPaddingEnd;
    private int tabPaddingBottom;

    private boolean tabTextBold;

    public Tab() {
    }

    @Nullable
    public Object getTag() {
        return this.tag;
    }

    @NonNull
    public Tab setTag(@Nullable Object tag) {
        this.tag = tag;
        return this;
    }

    public CharSequence getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(CharSequence contentDesc) {
        this.contentDesc = contentDesc;
    }

    public JTabLayout getParent() {
        return parent;
    }

    public void setParent(JTabLayout parent) {
        this.parent = parent;
    }

    public T getTabItem() {
        return tabItem;
    }

    public Tab setTabItem(T tabItem) {
        this.tabItem = tabItem;
        return this;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Nullable
    public CharSequence getText() {
        return this.text;
    }

    public ColorStateList getTabRippleColorStateList() {
        return tabRippleColorStateList;
    }

    public Tab setTabRippleColorStateList(ColorStateList tabRippleColorStateList) {
        this.tabRippleColorStateList = tabRippleColorStateList;
        if (this.parent != null && this.tabItem != null) {
            tabItem.updateBackgroundDrawable(parent.getContext());
        }
        return this;
    }

    public boolean isUnboundedRipple() {
        return unboundedRipple;
    }

    public Tab setUnboundedRipple(boolean unboundedRipple) {
        this.unboundedRipple = unboundedRipple;
        if (this.parent != null && this.tabItem != null) {
            tabItem.updateBackgroundDrawable(parent.getContext());
        }
        return this;
    }

    public Tab setTabPadding(int tabPaddingStart, int tabPaddingTop, int tabPaddingEnd, int tabPaddingBottom) {
        this.tabPaddingStart = tabPaddingStart;
        this.tabPaddingTop = tabPaddingTop;
        this.tabPaddingEnd = tabPaddingEnd;
        this.tabPaddingBottom = tabPaddingBottom;
        this.updateView();
        return this;
    }

    public int getTabPaddingStart() {
        return tabPaddingStart;
    }

    public int getTabPaddingTop() {
        return tabPaddingTop;
    }

    public int getTabPaddingEnd() {
        return tabPaddingEnd;
    }

    public int getTabPaddingBottom() {
        return tabPaddingBottom;
    }

    @NonNull
    public Tab setIcon(@DrawableRes int normalIcon, @DrawableRes int selectedIcon) {
        if (this.parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        } else {
            Drawable normalDrawable = ContextCompat.getDrawable(this.parent.getContext(), normalIcon);
            Drawable selectedDrawable = ContextCompat.getDrawable(this.parent.getContext(), selectedIcon);
            return setIcon(normalDrawable, selectedDrawable);
        }
    }

    @NonNull
    public Tab setIcon(@DrawableRes int icon) {
        if (this.parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        } else {
            Drawable drawable = ContextCompat.getDrawable(this.parent.getContext(), icon);
            return setIcon(drawable);
        }
    }

    @NonNull
    public Tab setIcon(Drawable icon) {
        return setIcon(icon, true);
    }

    @NonNull
    public Tab setIcon(Drawable icon, boolean refresh) {
        this.icon = icon;
        if (refresh) {
            this.updateView();
        }
        return this;
    }

    @NonNull
    public Tab setIcon(Drawable normalIcon, Drawable selectedIcon) {
        this.normalIcon = normalIcon;
        this.selectedIcon = selectedIcon;
        this.updateView();
        return this;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Drawable getNormalIcon() {
        return normalIcon;
    }

    public Drawable getSelectedIcon() {
        return selectedIcon;
    }

    public boolean getInline() {
        return inline;
    }

    public ColorStateList getTextColor() {
        return textColor;
    }

    @NonNull
    public Tab setText(@Nullable CharSequence text) {
        return setText(text, true);
    }


    @NonNull
    public Tab setText(@Nullable CharSequence text, boolean refresh) {
        if (this.tabItem != null) {
            if (TextUtils.isEmpty(this.contentDesc) && !TextUtils.isEmpty(text)) {
                this.tabItem.setContentDescription(text);
            }
        }
        this.text = text;
        if (refresh) {
            this.updateView();
        }
        return this;
    }

    @NonNull
    public Tab setText(@StringRes int resId) {
        if (this.parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        } else {
            return this.setText(this.parent.getResources().getText(resId));
        }
    }

    public Tab setTextColor(@ColorInt int defaultColor, @ColorInt int selectedColor) {
        return setTextColor(ColorUtils.createColorStateList(defaultColor, selectedColor));
    }

    public Tab setTextColor(ColorStateList textColor) {
        return setTextColor(textColor, true);
    }

    public Tab setTextColor(ColorStateList textColor, boolean refresh) {
        this.textColor = textColor;
        if (refresh) {
            this.updateView();
        }
        return this;
    }

    public Tab setTabBackgroundResId(int resId) {
        return setTabBackgroundResId(resId, true);
    }

    public Tab setTabBackgroundResId(int resId, boolean refresh) {
        this.tabBackgroundResId = resId;
        if (refresh) {
            if (this.parent != null && this.tabItem != null) {
                tabItem.updateBackgroundDrawable(parent.getContext());
            }
        }
        return this;
    }

    public int getTabBackgroundResId() {
        return tabBackgroundResId;
    }

    public PorterDuff.Mode getTabIconTintMode() {
        return tabIconTintMode;
    }

    public Tab setTabIconTintMode(PorterDuff.Mode tabIconTintMode) {
        return setTabIconTintMode(tabIconTintMode, true);
    }

    public Tab setTabIconTintMode(PorterDuff.Mode tabIconTintMode, boolean refresh) {
        this.tabIconTintMode = tabIconTintMode;
        if (refresh) {
            this.updateView();
        }
        return this;
    }

    public ColorStateList getTabIconTint() {
        return tabIconTint;
    }

    public Tab setTabIconTint(@ColorInt int defaultColor, @ColorInt int selectedColor) {
        return setTabIconTint(ColorUtils.createColorStateList(defaultColor, selectedColor));
    }

    public Tab setTabIconTint(ColorStateList tabIconTint) {
        return setTabIconTint(tabIconTint, true);
    }

    public Tab setTabIconTint(ColorStateList tabIconTint, boolean refresh) {
        this.tabIconTint = tabIconTint;
        if (refresh) {
            this.updateView();
        }
        return this;
    }

    public float getTabTextSize() {
        return tabTextSize;
    }

    public Tab setTabTextSize(float tabTextSize) {
        return setTabTextSize(tabTextSize, true);
    }

    public Tab setTabTextSize(float tabTextSize, boolean refresh) {
        this.tabTextSize = tabTextSize;
        if (refresh) {
            this.updateView();
        }
        return this;
    }

    @NonNull
    public Tab setInlineLabel(boolean inline) {
        this.inline = inline;
        if (this.tabItem != null) {
            tabItem.updateOrientation(inline);
        }
        return this;
    }

    public void select() {
        if (this.parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        } else {
            this.parent.selectTab(this);
        }
    }

    public boolean isSelected() {
        if (this.parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        } else {
            return this.parent.getSelectedTabPosition() == this.position;
        }
    }

    @NonNull
    public Tab setContentDescription(@StringRes int resId) {
        if (this.parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        } else {
            return this.setContentDescription(this.parent.getResources().getText(resId));
        }
    }

    @NonNull
    public Tab setContentDescription(@Nullable CharSequence contentDesc) {
        this.contentDesc = contentDesc;
        this.updateView();
        return this;
    }

    @Nullable
    public CharSequence getContentDescription() {
        return this.tabItem == null ? null : this.tabItem.getContentDescription();
    }

    public void updateView() {
        if (this.tabItem != null) {
            this.tabItem.update();
        }
    }

    public void reset() {
        this.parent = null;
        this.tabItem = null;
        this.tag = null;
        this.icon = null;
        this.normalIcon = null;
        this.selectedIcon = null;
        this.text = null;
        this.contentDesc = null;
        this.position = -1;
        this.textColor = null;
        this.tabIconTintMode = null;
        this.tabIconTint = null;
        this.tabTextSize = 0;
    }

    public Tab setTabTextBold(boolean isBold) {
        this.tabTextBold = isBold;
        this.updateView();
        return this;
    }

    public boolean isTabTextBold() {
        return tabTextBold;
    }
}


