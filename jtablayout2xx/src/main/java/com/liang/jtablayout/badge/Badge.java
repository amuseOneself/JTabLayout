package com.liang.jtablayout.badge;

public interface Badge {
    void show(String msg);

    void hide();

    void setBadgeTextSize(float sp);

    void setBadgeBackgroundColor(int color);

    void setBadgeStroke(int width, int color);

    void setBadgeTextColor(int color);
}
