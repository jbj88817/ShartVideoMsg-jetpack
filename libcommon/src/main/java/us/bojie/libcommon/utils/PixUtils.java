package us.bojie.libcommon.utils;

import android.util.DisplayMetrics;

import us.bojie.libcommon.AppGlobals;

public class PixUtils {
    public static int dp2px(int dpValue) {
        DisplayMetrics displayMetrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return (int) (displayMetrics.density * dpValue + 0.5f);
    }

    public static int getScreenWidth() {
        DisplayMetrics displayMetrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics displayMetrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
