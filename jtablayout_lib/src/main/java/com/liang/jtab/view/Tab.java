package com.liang.jtab.view;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface Tab {

    int getPosition();

    void setPosition(int position);

    CharSequence getTitle();

    Tab setTitle(CharSequence title);

    Object getContentDesc();

    Tab setContentDesc(Object contentDesc);

    ColorStateList getTitleColor();

    Tab setTitleColor(int defaultColor, int selectedColor);

    Tab setTitleColor(ColorStateList titleColor);

    Drawable[] getIcons();

    Tab setIcon(int defaultIcon, int selectedIcon);

    Tab setIcon(Drawable defaultIcon, Drawable selectedIcon);

    void setTabPadding(int paddingStart, int paddingTop, int paddingEnd, int paddingBottom);

    void transition(int textTransitionMode, float positionOffset);

    View getView();

    void setSelected(boolean b);

    void setTextTransitionMode(int textTransitionMode);

    Tab setBold(boolean b);

    Tab setTextSize(float size);
}
