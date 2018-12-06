package com.liang.jtab.utils;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtils {

    /**
     * Change from dip unit to PX (pixel) according to the resolution of mobile phone
     */
    public static int dip2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Convert the SP value to PX value to keep the text size unchanged
     */
    public static int sp2px(Context context, float sp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

}
