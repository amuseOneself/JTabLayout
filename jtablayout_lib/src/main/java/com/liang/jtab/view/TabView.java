package com.liang.jtab.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.jtab.R;
import com.liang.jtab.Tab;
import com.liang.jtab.utils.ColorUtils;
import com.liang.widget.BadgeView;

public class TabView extends FrameLayout implements Tab {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int mode = HORIZONTAL;

    private TextView titleView;
    private ImageView iconView;
    private BadgeView badgeView;

    private boolean bold;

    public static final int TRANSITION_MODE_NORMAL = 0;
    public static final int TRANSITION_MODE_SHADOW = 1;

    public static final int INVALID_POSITION = -1;
    private int position = INVALID_POSITION;
    private CharSequence title;
    private Object contentDesc;

    private ColorStateList titleColor;

    private Drawable[] icons;

    private int textTransitionMode = TRANSITION_MODE_NORMAL;

    private FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    private Drawable background;

    private View tabView;

    public TabView(@NonNull Context context) {
        this(context, null);

    }

    public TabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JTab,
                defStyleAttr, 0);

        if (typedArray.hasValue(R.styleable.JTab_jTabTitleColor)) {
            titleColor = typedArray.getColorStateList(com.liang.jtab.R.styleable.JTab_jTabTitleColor);
        }

        Drawable normalIcon = typedArray.getDrawable(R.styleable.JTab_jTabNormalIcon);
        Drawable selectedIcon = typedArray.getDrawable(R.styleable.JTab_jTabSelectedIcon);

        if (normalIcon != null || selectedIcon != null) {
            icons = new Drawable[2];
            icons[0] = normalIcon != null ? normalIcon : selectedIcon;
            icons[1] = selectedIcon != null ? selectedIcon : normalIcon;
        }

        mode = typedArray.getInt(R.styleable.JTab_jLayoutOrientation, HORIZONTAL);
        title = typedArray.getText(R.styleable.JTab_jTabTitle);

        updateLayout();
    }

    protected View initTabView() {
        return null;
    }

    private void updateLayout() {
        tabView = initTabView();
        if (tabView == null) {
            tabView = LayoutInflater.from(getContext()).inflate(
                    mode == VERTICAL ? R.layout.tab_menu_vertical : R.layout.tab_menu_horizontal, null, true);
        }

        iconView = setTabIconView();
        titleView = setTabTitleView();
        badgeView = setTabBadgeView();

        params.gravity = Gravity.CENTER;
        removeAllViews();
        addView(tabView, 0, params);
        updateView();
    }

    protected TextView setTabTitleView() {
        TextView title = tabView.findViewById(R.id.navigation_title);
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        return title;
    }

    protected ImageView setTabIconView() {
        return tabView.findViewById(R.id.navigation_icon);
    }

    protected BadgeView setTabBadgeView() {
        BadgeView badge = tabView.findViewById(R.id.navigation_badge);
        badge.setSingleLine(true);
        badge.setEllipsize(TextUtils.TruncateAt.END);
        badge.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        return badge;
    }

    private void updateView() {
        if (titleView != null) {
            if (!TextUtils.isEmpty(getTitle())) {
                titleView.setText(getTitle());
                if (bold) {
                    titleView.setTypeface(Typeface.defaultFromStyle(isSelected() ? Typeface.BOLD : Typeface.NORMAL));
                }
                titleView.setVisibility(VISIBLE);
            } else {
                titleView.setVisibility(GONE);
            }
        }

        if (iconView != null) {
            if (getIcons() != null) {
                iconView.setImageDrawable(isSelected() ? getIcons()[1] : getIcons()[0]);
                iconView.setVisibility(VISIBLE);
            } else {
                iconView.setVisibility(GONE);
            }
        }
    }

    private void initTitleColors() {
        if (getTitleColor() != null && titleView != null) {
            if (textTransitionMode == TRANSITION_MODE_SHADOW) {
                titleView.setTextColor(isSelected() ? getTitleColor().getColorForState(SELECTED_STATE_SET, Color.GRAY) :
                        getTitleColor().getColorForState(EMPTY_STATE_SET, Color.GRAY));
                return;
            }
            titleView.setTextColor(getTitleColor());
            titleView.setSelected(isSelected());
        }
    }

    @Override
    public void setSelected(final boolean selected) {
        final boolean changed = isSelected() != selected;
        super.setSelected(selected);
        if (changed && selected && Build.VERSION.SDK_INT < 16) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        }
        if (titleView != null && titleView.getVisibility() != GONE) {
            if (bold) {
                titleView.setTypeface(Typeface.defaultFromStyle(isSelected() ? Typeface.BOLD : Typeface.NORMAL));
            }
            titleView.setSelected(selected);
            if (getTitleColor() != null && textTransitionMode == TRANSITION_MODE_SHADOW) {
                titleView.setTextColor(isSelected() ? getTitleColor().getColorForState(SELECTED_STATE_SET, Color.GRAY) :
                        getTitleColor().getColorForState(EMPTY_STATE_SET, Color.GRAY));
            }
        }
        if (getIcons() != null && iconView != null && iconView.getVisibility() != GONE) {
            iconView.setImageDrawable(isSelected() ? getIcons()[1]
                    : getIcons()[0]);
        }
    }

    @Override
    public void setTextTransitionMode(int textTransitionMode) {
        this.textTransitionMode = textTransitionMode;
        initTitleColors();
    }

    @Override
    public Tab setOrientationMode(int mode) {
        this.mode = mode;
        updateLayout();
        return this;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    @Override
    public Tab setTitle(CharSequence title) {
        this.title = title;
        updateView();
        return this;
    }

    @Override
    public Object getContentDesc() {
        return contentDesc;
    }

    @Override
    public Tab setContentDesc(Object contentDesc) {
        this.contentDesc = contentDesc;
        return this;
    }

    @Override
    public ColorStateList getTitleColor() {
        return titleColor;
    }

    @Override
    public Tab setTitleColor(@ColorInt int defaultColor, @ColorInt int selectedColor) {
        return setTitleColor(ColorUtils.createColorStateList(defaultColor, selectedColor));
    }

    @Override
    public Tab setTitleColor(ColorStateList titleColor) {
        this.titleColor = titleColor;
        initTitleColors();
        return this;
    }

    @Override
    public Tab setBackgroundRes(int resId) {
        return setBackgroundDraw(ContextCompat.getDrawable(getContext(), resId));
    }

    @SuppressLint("NewApi")
    @Override
    public Tab setBackgroundDraw(Drawable background) {
        this.background = background;
        setBackground(background);
        setSelected(isSelected());
        return this;
    }

    @Override
    public Drawable getBackground() {
        return background;
    }

    @Override
    public Drawable[] getIcons() {
        return icons;
    }

    @Override
    public Tab setIcon(@DrawableRes int defaultIcon, @DrawableRes int selectedIcon) {
        Drawable defaultDrawable = ContextCompat.getDrawable(getContext(), defaultIcon);
        Drawable selectedDrawable = ContextCompat.getDrawable(getContext(), selectedIcon);
        return setIcon(defaultDrawable, selectedDrawable);
    }

    @Override
    public Tab setIcon(Drawable defaultIcon, Drawable selectedIcon) {
        if (defaultIcon != null && selectedIcon != null) {
            icons = new Drawable[2];
            icons[0] = defaultIcon;
            icons[1] = selectedIcon;
        }
        updateView();
        return this;
    }

    @Override
    public void setTabPadding(int left, int top, int right, int bottom) {
        setPadding(left, top, right, bottom);
    }

    @Override
    public void transition(int textTransitionMode, float positionOffset) {
        if (getTitleColor() == null || textTransitionMode == TRANSITION_MODE_NORMAL) {
            return;
        }

        titleView.setTextColor(ColorUtils.getColorFrom(getTitleColor().getColorForState(EMPTY_STATE_SET, Color.GRAY),
                getTitleColor().getColorForState(SELECTED_STATE_SET, Color.GRAY), positionOffset));
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public Tab setBold(boolean bold) {
        this.bold = bold;
        if (bold && titleView != null) {
            titleView.setTypeface(Typeface.defaultFromStyle(isSelected() ? Typeface.BOLD : Typeface.NORMAL));
        }
        return this;
    }

    @Override
    public Tab setTextSize(float size) {
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        return this;
    }

    @Override
    public Tab setBadgeTextColor(@ColorInt int color) {
        if (badgeView != null) {
            badgeView.setTextColor(color);
        }
        return this;
    }

    @Override
    public Tab setBadgeTextSize(float size) {
        if (badgeView != null) {
            badgeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        return this;
    }

    @Override
    public Tab setBadgeColor(@ColorInt int color) {
        if (badgeView != null) {
            badgeView.setBackgroundColor(color);
        }
        return this;
    }

    @Override
    public Tab setBadgeStroke(int width, @ColorInt int color) {
        if (badgeView != null) {
            badgeView.setStroke(width, color);
        }
        return this;
    }


    @Override
    public void showBadgeMsg(String msg, boolean showDot) {
        if (badgeView == null) {
            return;
        }
        badgeView.show(showDot ? "" : msg);

    }

    @Override
    public void hideBadgeMsg() {
        if (badgeView == null) {
            return;
        }

        badgeView.hide();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TabView)) {
            return false;
        }
        TabView tab = (TabView) obj;
        return tab.getPosition() == getPosition();
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getPosition()).hashCode();
    }
}
