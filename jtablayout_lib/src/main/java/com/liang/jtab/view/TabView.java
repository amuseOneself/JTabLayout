package com.liang.jtab.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.jtab.R;

public class TabView extends Tab {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int mode = HORIZONTAL;

    private TextView titleView;
    private ImageView iconView;
    private TextView badgeView;

    private boolean bold;

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
        badgeView = view.findViewById(R.id.navigation_badge);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        badgeView.setSingleLine(true);
        badgeView.setEllipsize(TextUtils.TruncateAt.END);
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
        if (getTitleColor() != null && titleView != null) {
            titleView.setTextColor(getTitleColor());
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
        }
        if (getIcons() != null && iconView != null && iconView.getVisibility() != GONE) {
            iconView.setImageDrawable(isSelected() ? getIcons()[1]
                    : getIcons()[0]);
        }
    }


    public TabView setMode(int mode) {
        this.mode = mode;
        updateLayout();
        return this;
    }

    @Override
    public TabView setTitle(CharSequence title) {
        super.setTitle(title);
        updateView();
        return this;
    }

    @Override
    public TabView setTitleColor(ColorStateList titleColor) {
        super.setTitleColor(titleColor);
        updateView();
        return this;
    }


    @Override
    public TabView setIcon(Drawable defaultIcon, Drawable selectedIcon) {
        super.setIcon(defaultIcon, selectedIcon);
        updateView();
        return this;
    }

    @Override
    public Tab setIcon(int defaultIcon, int selectedIcon) {
        super.setIcon(defaultIcon, selectedIcon);
        updateView();
        return this;
    }

    public Tab setTitleBold(boolean bold) {
        this.bold = bold;
        updateView();
        return this;
    }
}
