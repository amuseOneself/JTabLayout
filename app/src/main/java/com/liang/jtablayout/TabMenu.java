package com.liang.jtablayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.liang.jtab.view.BadgeView;
import com.liang.jtab.view.TabView;

public class TabMenu extends TabView {

    private View tabView;

    public TabMenu(@NonNull Context context) {
        super(context);
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
    protected BadgeView setTabBadgeView() {
        BadgeView badge = tabView.findViewById(R.id.navigation_badge);
        badge.setSingleLine(true);
        badge.setEllipsize(TextUtils.TruncateAt.END);
        badge.setTextSize(30);
        return badge;
    }
}
