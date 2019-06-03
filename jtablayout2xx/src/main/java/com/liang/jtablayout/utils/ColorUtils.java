package com.liang.jtablayout.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;

/**
 * Calculate the excess color of the start color gradient to the end color gradient
 */
public class ColorUtils {
    public static int getColorFrom(int startColor, int endColor, float positionOffset) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * positionOffset + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * positionOffset + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * positionOffset + 0.5));
        return Color.argb(255, red, greed, blue);
    }

    public static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        int[][] states = new int[2][];
        int[] colors = new int[2];
        int i = 0;
        states[i] = new int[] { android.R.attr.state_selected };
        colors[i] = selectedColor;
        i++;
        states[i] = new int[] {};
        colors[i] = defaultColor;
        i++;
        return new ColorStateList(states, colors);
    }

}
