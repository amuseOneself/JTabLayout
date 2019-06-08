package com.liang.jtablayout.adapter;

import com.liang.jtablayout.tab.Tab;
import com.liang.jtablayout.tab.TabChild;

public interface TabAdapter {
    <T extends TabChild> Tab<T> getTab(int position);
}
