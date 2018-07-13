package com.liang.jtab.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.widget.FrameLayout;

public abstract class Tab extends FrameLayout {

    public static final int INVALID_POSITION = -1;
    private int position = INVALID_POSITION;
    private CharSequence title;
    private Object contentDesc;

    private ColorStateList titleColor;

    private Drawable[] icons;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CharSequence getTitle() {
        return title;
    }

    public Tab setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public Object getContentDesc() {
        return contentDesc;
    }

    public Tab setContentDesc(Object contentDesc) {
        this.contentDesc = contentDesc;
        return this;
    }

    public ColorStateList getTitleColor() {
        return titleColor;
    }

    public Tab setTitleColor(int defaultColor, int selectedColor) {
        return setTitleColor(createColorStateList(defaultColor, selectedColor));
    }

    public Tab setTitleColor(ColorStateList titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public Drawable[] getIcons() {
        return icons;
    }

    public Tab setIcon(int defaultIcon, int selectedIcon) {
        Drawable defaultDrawable = ContextCompat.getDrawable(getContext(), defaultIcon);
        Drawable selectedDrawable = ContextCompat.getDrawable(getContext(), selectedIcon);
        return setIcon(defaultDrawable, selectedDrawable);
    }

    public Tab setIcon(Drawable defaultIcon, Drawable selectedIcon) {
        if (defaultIcon != null && selectedIcon != null) {
            icons = new Drawable[2];
            icons[0] = defaultIcon;
            icons[1] = selectedIcon;
        }
        return this;
    }

    public Tab(@NonNull Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);
    }

    public void setTabPadding(int mTabPaddingStart, int mTabPaddingTop, int mTabPaddingEnd, int mTabPaddingBottom) {
        ViewCompat.setPaddingRelative(this, mTabPaddingStart, mTabPaddingTop,
                mTabPaddingEnd, mTabPaddingBottom);
    }


    private ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;
        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        i++;
        return new ColorStateList(states, colors);
    }
}
