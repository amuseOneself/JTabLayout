package com.liang.jtab;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.View;

public interface Tab {

    /**
     * 设置Tab的排列方式
     *
     * @param mode
     */
    Tab setOrientationMode(int mode);

    int getPosition();

    void setPosition(int position);

    CharSequence getTitle();

    /**
     * 设置Tab的标题
     *
     * @param title
     */
    Tab setTitle(CharSequence title);

    Object getContentDesc();

    /**
     * Tab的自定义扩展内容
     *
     * @param contentDesc
     */
    Tab setContentDesc(Object contentDesc);

    ColorStateList getTitleColor();

    /**
     * Tab的标题切换颜色
     *
     * @param defaultColor
     * @param selectedColor
     */
    Tab setTitleColor(@ColorInt int defaultColor, @ColorInt int selectedColor);

    /**
     * Tab的标题切换颜色
     *
     * @param titleColor
     */
    Tab setTitleColor(ColorStateList titleColor);

    Tab setBackgroundRes(@DrawableRes int resId);

    Tab setBackgroundDraw(Drawable background);

    Drawable getBackground();

    Drawable[] getIcons();

    /**
     * Tab的切换图标
     *
     * @param defaultIcon
     * @param selectedIcon
     */
    Tab setIcon(@DrawableRes int defaultIcon, @DrawableRes int selectedIcon);

    /**
     * Tab的切换图标
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
     * Tab的标题颜色切换方式
     *
     * @param textTransitionMode
     */
    void setTextTransitionMode(int textTransitionMode);

    /**
     * 设置Tab的标题选中时变粗
     *
     * @param b
     */
    Tab setBold(boolean b);

    Tab setTextSize(float size);

    /**
     * 设置Badge的字体颜色
     *
     * @param color
     */
    Tab setBadgeTextColor(@ColorInt int color);

    /**
     * 设置Badge的字体大小
     *
     * @param size
     */
    Tab setBadgeTextSize(float size);

    /**
     * 设置Badge的背景颜色
     *
     * @param color
     */
    Tab setBadgeColor(@ColorInt int color);

    /**
     * 设置Badge的边框和颜色
     *
     * @param width
     * @param color
     */
    Tab setBadgeStroke(int width, @ColorInt int color);

    void showBadgeMsg(String msg, boolean showDot);

    void hideBadgeMsg();
}
