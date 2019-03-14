package com.liang.jtablayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.jtab.view.TabView;
import com.liang.widget.BadgeView;

public class TabMenu extends TabView {

    private View tabView;

    public TabMenu(@NonNull Context context) {
        super(context);
    }

    public TabMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TabMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View initTabView() {
        tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_menul, null, true);
        return tabView;
    }

    @Override
    protected TextView setTabTitleView() {
        TextView title = tabView.findViewById(R.id.navigation_title);
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        return title;
    }

    @Override
    protected ImageView setTabIconView() {
        return null;
    }

    @Override
    protected BadgeView setTabBadgeView() {
        BadgeView badge = tabView.findViewById(R.id.navigation_badge);
        return badge;
    }
}
