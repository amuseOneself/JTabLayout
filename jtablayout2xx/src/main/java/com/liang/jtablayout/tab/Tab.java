package com.liang.jtablayout.tab;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.liang.jtablayout.JTabLayout;
import com.liang.jtablayout.utils.ColorUtils;

public class Tab<T extends TabItem> {
    public static final int INVALID_POSITION = -1;
    private Object tag;
    private Drawable normalIcon;
    private Drawable selectedIcon;
    private CharSequence text;
    private CharSequence contentDesc;
    private int position = -1;
    public JTabLayout parent;
    public T tabItem;
    private boolean inline;
    private ColorStateList textColor;

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

    public void setTabItem(T tabItem) {
        this.tabItem = tabItem;
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
    public Tab setIcon(Drawable normalIcon, Drawable selectedIcon) {
        this.normalIcon = normalIcon;
        this.selectedIcon = selectedIcon;
        this.updateView();
        return this;
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
        if (this.tabItem != null) {
            if (TextUtils.isEmpty(this.contentDesc) && !TextUtils.isEmpty(text)) {
                this.tabItem.setContentDescription(text);
            }
        }
        this.text = text;
        this.updateView();
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
        this.textColor = textColor;
        this.updateView();
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
        this.normalIcon = null;
        this.selectedIcon = null;
        this.text = null;
        this.contentDesc = null;
        this.position = -1;
    }
}
