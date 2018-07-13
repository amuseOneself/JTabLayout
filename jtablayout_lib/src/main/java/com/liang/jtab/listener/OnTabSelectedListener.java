package com.liang.jtab.listener;

public interface OnTabSelectedListener {
    void onTabSelected(int position);

    void onTabUnselected(int position);

    void onTabReselected(int position);
}
