package com.liang.jtablayout.ripple;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.ColorUtils;
import android.util.StateSet;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class RippleUtils {
    public static final boolean USE_FRAMEWORK_RIPPLE;
    private static final int[] PRESSED_STATE_SET;
    private static final int[] HOVERED_FOCUSED_STATE_SET;
    private static final int[] FOCUSED_STATE_SET;
    private static final int[] HOVERED_STATE_SET;
    private static final int[] SELECTED_PRESSED_STATE_SET;
    private static final int[] SELECTED_HOVERED_FOCUSED_STATE_SET;
    private static final int[] SELECTED_FOCUSED_STATE_SET;
    private static final int[] SELECTED_HOVERED_STATE_SET;
    private static final int[] SELECTED_STATE_SET;

    private RippleUtils() {
    }

    @NonNull
    public static ColorStateList convertToRippleDrawableColor(@Nullable ColorStateList rippleColor) {
        byte size;
        int[][] states;
        int[] colors;
//        byte i;
        int i;
        if (USE_FRAMEWORK_RIPPLE) {
            size = 2;
            states = new int[size][];
            colors = new int[size];
            i = 0;
            states[i] = SELECTED_STATE_SET;
            colors[i] = getColorForState(rippleColor, SELECTED_PRESSED_STATE_SET);
            i = i + 1;
            states[i] = StateSet.NOTHING;
            colors[i] = getColorForState(rippleColor, PRESSED_STATE_SET);
            ++i;
            return new ColorStateList(states, colors);
        } else {
            size = 10;
            states = new int[size][];
            colors = new int[size];
            i = 0;
            states[i] = SELECTED_PRESSED_STATE_SET;
            colors[i] = getColorForState(rippleColor, SELECTED_PRESSED_STATE_SET);
            i = i + 1;
            states[i] = SELECTED_HOVERED_FOCUSED_STATE_SET;
            colors[i] = getColorForState(rippleColor, SELECTED_HOVERED_FOCUSED_STATE_SET);
            ++i;
            states[i] = SELECTED_FOCUSED_STATE_SET;
            colors[i] = getColorForState(rippleColor, SELECTED_FOCUSED_STATE_SET);
            ++i;
            states[i] = SELECTED_HOVERED_STATE_SET;
            colors[i] = getColorForState(rippleColor, SELECTED_HOVERED_STATE_SET);
            ++i;
            states[i] = SELECTED_STATE_SET;
            colors[i] = 0;
            ++i;
            states[i] = PRESSED_STATE_SET;
            colors[i] = getColorForState(rippleColor, PRESSED_STATE_SET);
            ++i;
            states[i] = HOVERED_FOCUSED_STATE_SET;
            colors[i] = getColorForState(rippleColor, HOVERED_FOCUSED_STATE_SET);
            ++i;
            states[i] = FOCUSED_STATE_SET;
            colors[i] = getColorForState(rippleColor, FOCUSED_STATE_SET);
            ++i;
            states[i] = HOVERED_STATE_SET;
            colors[i] = getColorForState(rippleColor, HOVERED_STATE_SET);
            ++i;
            states[i] = StateSet.NOTHING;
            colors[i] = 0;
            ++i;
            return new ColorStateList(states, colors);
        }
    }

    @ColorInt
    private static int getColorForState(@Nullable ColorStateList rippleColor, int[] state) {
        int color;
        if (rippleColor != null) {
            color = rippleColor.getColorForState(state, rippleColor.getDefaultColor());
        } else {
            color = 0;
        }

        return USE_FRAMEWORK_RIPPLE ? doubleAlpha(color) : color;
    }

    @ColorInt
    @TargetApi(21)
    private static int doubleAlpha(@ColorInt int color) {
        int alpha = Math.min(2 * Color.alpha(color), 255);
        return ColorUtils.setAlphaComponent(color, alpha);
    }

    static {
        USE_FRAMEWORK_RIPPLE = Build.VERSION.SDK_INT >= 21;
        PRESSED_STATE_SET = new int[]{16842919};
        HOVERED_FOCUSED_STATE_SET = new int[]{16843623, 16842908};
        FOCUSED_STATE_SET = new int[]{16842908};
        HOVERED_STATE_SET = new int[]{16843623};
        SELECTED_PRESSED_STATE_SET = new int[]{16842913, 16842919};
        SELECTED_HOVERED_FOCUSED_STATE_SET = new int[]{16842913, 16843623, 16842908};
        SELECTED_FOCUSED_STATE_SET = new int[]{16842913, 16842908};
        SELECTED_HOVERED_STATE_SET = new int[]{16842913, 16843623};
        SELECTED_STATE_SET = new int[]{16842913};
    }
}