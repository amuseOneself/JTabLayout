package com.liang.jtab.listener;

public interface OnTabSelectedListener {

    /**
     * 当Tab进入选定状态时调用
     *
     * @param position The tab that was selected
     */
    void onTabSelected(int position);

    /**
     * 当用户再次选择已经选择的Tab时调用
     *
     * @param position The tab that was reselected
     */
    void onTabReselected(int position);
}
