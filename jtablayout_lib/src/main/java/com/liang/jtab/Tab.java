package com.liang.jtab;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.View;

public interface Tab {

    /**
     * Setting Tab's Arrangement
     *
     * @param mode
     */
    Tab setOrientationMode(int mode);

    int getPosition();

    void setPosition(int position);

    CharSequence getTitle();

    /**
     * Set the title of Tab.
     *
     * @param title
     */
    Tab setTitle(CharSequence title);

    Object getContentDesc();

    /**
     * Tab's custom extension content
     *
     * @param contentDesc
     */
    Tab setContentDesc(Object contentDesc);

    ColorStateList getTitleColor();

    /**
     * Tab Title Switching Colors
     *
     * @param defaultColor
     * @param selectedColor
     */
    Tab setTitleColor(@ColorInt int defaultColor, @ColorInt int selectedColor);

    /**
     * Tab Title Switching Colors
     *
     * @param titleColor
     */
    Tab setTitleColor(ColorStateList titleColor);

    Tab setBackgroundRes(@DrawableRes int resId);

    Tab setBackgroundDraw(Drawable background);

    Drawable getBackground();

    Drawable[] getIcons();

    /**
     * Tab toggle Icon
     *
     * @param defaultIcon
     * @param selectedIcon
     */
    Tab setIcon(@DrawableRes int defaultIcon, @DrawableRes int selectedIcon);

    /**
     * Tab toggle Icon
     *
     * @param defaultIcon
     * @param selectedIcon
     */
    Tab setIcon(Drawable defaultIcon, Drawable selectedIcon);

    void setTabPadding(int left, int top, int right, int bottom);

    void transition(int textTransitionMode, float positionOffset);

    View getView();

    void setSelected(boolean b);

    /**
     * Tab's Title Color Switching Mode
     *
     * @param textTransitionMode
     */
    void setTextTransitionMode(int textTransitionMode);

    /**
     * Setting Tab Title Selection Time-Varying Coarseness
     *
     * @param b
     */
    Tab setBold(boolean b);

    Tab setTextSize(float size);

    /**
     * Setting the font color of Badge
     *
     * @param color
     */
    Tab setBadgeTextColor(@ColorInt int color);

    /**
     * Set the font size of Badge
     *
     * @param size
     */
    Tab setBadgeTextSize(float size);

    /**
     * Setting the background color of Badge
     *
     * @param color
     */
    Tab setBadgeColor(@ColorInt int color);

    /**
     * Set the border and color of the Badge
     *
     * @param width
     * @param color
     */
    Tab setBadgeStroke(int width, @ColorInt int color);

    void showBadgeMsg(String msg, boolean showDot);

    void hideBadgeMsg();
}
