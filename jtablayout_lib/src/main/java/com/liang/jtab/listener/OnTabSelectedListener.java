package com.liang.jtab.listener;

public interface OnTabSelectedListener {

    /**
     * Called when Tab enters the selected state
     *
     * @param position The tab that was selected
     */
    void onTabSelected(int position);

    /**
     * Called when the user selects the selected Tab again
     *
     * @param position The tab that was reselected
     */
    void onTabReselected(int position);
}
