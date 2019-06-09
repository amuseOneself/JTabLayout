package com.liang.jtablayout.tab;


import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.view.View;

public interface TabChild {

    void setContentDescription(CharSequence text);

    CharSequence getContentDescription();

    void updateBackgroundDrawable(Context context);

    void drawBackground(Canvas canvas);

    void setTab(@Nullable Tab tab);

    void reset();

    void update();

    void updateOrientation(boolean inline);

    View getView();

    int getContentWidth();

    void showBadge(String msg);

    void hideBadge();

    void updateScaleAndColor(float offset);
}
