package com.liang.jtablayout.tab;


import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.view.View;

public interface TabItem {

    void setContentDescription(CharSequence text);

    CharSequence getContentDescription();

    void drawBackground(Canvas canvas);

    void setTab(@Nullable Tab tab);

    void reset();

    void update();

    void updateOrientation(boolean inline);

    View getView();

    int getContentWidth();
}
