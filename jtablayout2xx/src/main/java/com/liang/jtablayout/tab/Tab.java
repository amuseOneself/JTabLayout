package com.liang.jtablayout.tab;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.liang.widget.JTabLayout;

public interface Tab {

    void initTabView();

    void setTag(@Nullable Object tag);

    @Nullable
    Object getTag();

    Object getObject();

    Tab setObject(Object object);

    int getPosition();

    Tab setPosition(int position);

    boolean getInline();

    Tab setInlineLabel(boolean inline);

    CharSequence getTitle();

    Tab setTitle(CharSequence text);

    Tab setTitle(@StringRes int resId);

    Drawable getIcon();

    Drawable getNormalIcon();

    Drawable getSelectedIcon();

    Tab setIcon(@DrawableRes int icon);

    Tab setIcon(Drawable icon);

    Tab setIcon(@DrawableRes int normalIcon, @DrawableRes int selectedIcon);

    Tab setIcon(Drawable normalIcon, Drawable selectedIcon);

    ColorStateList getTitleColor();

    Tab setTitleColor(@ColorInt int defaultColor, @ColorInt int selectedColor);

    Tab setTitleColor(ColorStateList textColor);

    ColorStateList getTabIconTint();

    Tab setTabIconTint(@ColorInt int defaultColor, @ColorInt int selectedColor);

    Tab setTabIconTint(ColorStateList tabIconTint);

    PorterDuff.Mode getTabIconTintMode();

    Tab setTabIconTintMode(PorterDuff.Mode tabIconTintMode);

    int getTabBackgroundResId();

    Tab setTabBackgroundResId(int resId);

    JTabLayout getTabLayout();

    float getTabTextSize();

    Tab setTabTextSize(float sizePx);

    boolean isTabTextBold();

    Tab setTabTextBold(boolean isBold);

    CharSequence getContentDescription();

    void setContentDescription(@StringRes int resId);

    void setContentDescription(@Nullable CharSequence contentDesc);

    ColorStateList getTabRippleColorStateList();

    Tab setTabRippleColorStateList(ColorStateList tabRippleColorStateList);

    boolean isUnboundedRipple();

    Tab setUnboundedRipple(boolean unboundedRipple);

    Tab setTabPadding(int tabPaddingStart, int tabPaddingTop, int tabPaddingEnd, int tabPaddingBottom);

    int getTabPaddingStart();

    int getTabPaddingTop();

    int getTabPaddingEnd();

    int getTabPaddingBottom();

    Tab setTabLayout(JTabLayout tabLayout);

    void select();

    boolean isSelected();

    void updateView();

    void updateBackgroundDrawable();

    void drawBackground(Canvas canvas);

    View getView();

    int getContentWidth();

    void showBadge(String msg);

    void hideBadge();

    void updateColor(float offset);

    void updateScale(float scale);

    void setBadgeTextColor(int color);

    void setBadgeTextSize(float sp);

    void setBadgeBackgroundColor(int color);

    void setBadgeStroke(int width, int color);

    void reset();
}
