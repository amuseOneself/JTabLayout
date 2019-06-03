package com.liang.jtablayout.tab;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.TooltipCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.liang.jtablayoutx.R;

public class TabView extends FrameLayout implements TabItem {

    private Tab tab;
    private TextView textView;
    private ImageView iconView;
    @Nullable
    private Drawable baseBackgroundDrawable;
    private int defaultMaxLines = 2;
    private FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

    public TabView(Context context) {
        super(context);
        this.setFocusable(true);
        this.setClickable(true);
        setBackgroundColor(Color.GREEN);
    }

    private void updateLayout() {
        removeAllViews();
        View view = LayoutInflater.from(getContext()).inflate(
                tab.getInline() ? R.layout.tab_item_horizontal : R.layout.tab_item_vertical, this, true);

        iconView = view.findViewById(R.id.tab_icon);
        textView = view.findViewById(R.id.tab_title);
//        badgeView = setTabBadgeView();
//        addView(view);

        ViewCompat.setPaddingRelative(this, tab.parent.tabPaddingStart, tab.parent.tabPaddingTop, tab.parent.tabPaddingEnd, tab.parent.tabPaddingBottom);

        update();
    }

    @Override
    public void drawBackground(Canvas canvas) {
        if (this.baseBackgroundDrawable != null) {
            this.baseBackgroundDrawable.setBounds(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
            this.baseBackgroundDrawable.draw(canvas);
        }

    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        boolean changed = false;
        int[] state = this.getDrawableState();
        if (this.baseBackgroundDrawable != null && this.baseBackgroundDrawable.isStateful()) {
            changed |= this.baseBackgroundDrawable.setState(state);
        }

        if (changed) {
            this.invalidate();
            tab.parent.invalidate();
        }
    }

    @Override
    public boolean performClick() {
        boolean handled = super.performClick();
        if (this.tab != null) {
            if (!handled) {
                this.playSoundEffect(0);
            }

            this.tab.select();
            return true;
        } else {
            return handled;
        }
    }

    @Override
    public void setSelected(boolean selected) {
        boolean changed = this.isSelected() != selected;
        super.setSelected(selected);
        if (changed && selected && Build.VERSION.SDK_INT < 16) {
            this.sendAccessibilityEvent(4);
        }

        if (this.textView != null) {
            this.textView.setSelected(selected);
        }

        if (this.iconView != null) {
            this.iconView.setSelected(selected);
            Drawable normalIcon = tab != null ? tab.getNormalIcon() : null;
            Drawable selectedIcon = tab != null ? tab.getSelectedIcon() : null;
            if (normalIcon == null && selectedIcon == null) {
                this.iconView.setVisibility(GONE);
            } else {
                this.iconView.setVisibility(VISIBLE);
                this.iconView.setImageDrawable(selected ? selectedIcon != null ? selectedIcon : normalIcon : normalIcon != null ? normalIcon : selectedIcon);
            }
        }

    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(android.support.v7.app.ActionBar.Tab.class.getName());
    }

    @TargetApi(14)
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(android.support.v7.app.ActionBar.Tab.class.getName());
    }

    @Override
    public void setTab(@Nullable Tab tab) {
        if (tab != null && tab != this.tab) {
            this.tab = tab;
            updateLayout();
        }
    }

    @Override
    public void reset() {
        this.setTab(null);
        this.setSelected(false);
    }

    @Override
    public void update() {
        Tab tab = this.tab;

//        TextViewCompat.setTextAppearance(this.textView, tab.parent.tabTextAppearance);
        if (textView != null) {
            if (tab.getTextColor() != null) {
                this.textView.setTextColor(tab.getTextColor());
            }
        }

        updateTextAndIcon(this.textView, this.iconView);

        if (tab != null && !TextUtils.isEmpty(tab.getContentDesc())) {
            this.setContentDescription(tab.getContentDesc());
        }

        this.setSelected(tab != null && tab.isSelected());
    }

    @Override
    public void updateOrientation(boolean inline) {
        this.updateLayout();
    }

    @Override
    public View getView() {
        return this;
    }

    private void updateTextAndIcon(@Nullable TextView textView, @Nullable ImageView iconView) {
        if (textView == null && iconView == null) {
            return;
        }

        Drawable normalIcon = tab != null ? tab.getNormalIcon() : null;
        Drawable selectedIcon = tab != null ? tab.getSelectedIcon() : null;
        if (normalIcon == null && selectedIcon == null) {
            iconView.setVisibility(GONE);
            iconView.setImageDrawable(null);
        } else {
            iconView.setVisibility(VISIBLE);
            iconView.setImageDrawable(iconView.isSelected() ? selectedIcon != null ? selectedIcon : normalIcon : normalIcon != null ? normalIcon : selectedIcon);
            this.setVisibility(VISIBLE);
        }

        CharSequence text = this.tab != null ? this.tab.getText() : null;
        boolean hasText = !TextUtils.isEmpty(text);

        if (textView != null) {
            if (hasText) {
                textView.setText(text);
                textView.setVisibility(VISIBLE);
                this.setVisibility(VISIBLE);
            } else {
                textView.setVisibility(GONE);
                textView.setText(null);
            }

        }

        CharSequence contentDesc = this.tab != null ? this.tab.getContentDesc() : null;
        TooltipCompat.setTooltipText(this, hasText ? null : contentDesc);
    }

    @Override
    public int getContentWidth() {
        boolean initialized = false;
        int left = 0;
        int right = 0;
        View[] var4 = new View[]{this.textView, this.iconView};
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            View view = var4[var6];
            if (view != null && view.getVisibility() == VISIBLE) {
                left = initialized ? Math.min(left, view.getLeft()) : view.getLeft();
                right = initialized ? Math.max(right, view.getRight()) : view.getRight();
                initialized = true;
            }
        }

        return right - left;
    }

    public Tab getTab() {
        return this.tab;
    }

    private float approximateLineWidth(Layout layout, int line, float textSize) {
        return layout.getLineWidth(line) * (textSize / layout.getPaint().getTextSize());
    }

    int dpToPx(@Dimension(unit = 0) int dps) {
        return Math.round(this.getResources().getDisplayMetrics().density * (float) dps);
    }
}
