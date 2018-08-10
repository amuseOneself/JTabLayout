package com.liang.jtab.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.jtab.R;
import com.liang.jtab.utils.ColorUtils;

public class TabView extends FrameLayout implements Tab {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int mode = HORIZONTAL;

    private TextView titleView;
    private ImageView iconView;
    private TextView badgeView;

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

    public TabView(@NonNull Context context) {
        super(context);
        setFocusable(true);
        updateLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void updateLayout() {
        View view = LayoutInflater.from(getContext()).inflate(
                mode == VERTICAL ? R.layout.tab_menu_vertical : R.layout.tab_menu_horizontal, null, true);
        iconView = view.findViewById(R.id.navigation_icon);
        titleView = view.findViewById(R.id.navigation_title);
//        badgeView = view.findViewById(R.id.navigation_badge);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
//        badgeView.setSingleLine(true);
//        badgeView.setEllipsize(TextUtils.TruncateAt.END);
        params.gravity = Gravity.CENTER;
        addView(view, 0, params);
        updateView();
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
            titleView.setSelected(selected);
            if (bold) {
                titleView.setTypeface(Typeface.defaultFromStyle(isSelected() ? Typeface.BOLD : Typeface.NORMAL));
            }
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


    public TabView setMode(int mode) {
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
    public TabView setTitle(CharSequence title) {
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
    public Tab setTitleColor(int defaultColor, int selectedColor) {
        return setTitleColor(createColorStateList(defaultColor, selectedColor));
    }

    @Override
    public Tab setTitleColor(ColorStateList titleColor) {
        this.titleColor = titleColor;
        initTitleColors();
        return this;
    }

    @Override
    public Drawable[] getIcons() {
        return icons;
    }

    @Override
    public Tab setIcon(int defaultIcon, int selectedIcon) {
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
    public void setTabPadding(int start, int top, int end, int bottom) {
        ViewCompat.setPaddingRelative(this, start, top, end, bottom);
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
        titleView.setTextSize(size);
        return this;
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
