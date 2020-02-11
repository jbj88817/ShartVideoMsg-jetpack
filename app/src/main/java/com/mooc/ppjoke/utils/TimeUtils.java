package com.mooc.ppjoke.utils;

import java.util.Calendar;

public class TimeUtils {
    public static String calculate(long time) {
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        long diff = (timeInMillis - time) / 1000;
        if (diff < 60) {
            return diff + " secs ago";
        } else if (diff < 3600) {
            return diff / 60 + " mins ago";
        } else if (diff < 3600 * 24) {
            return diff / 3600 + " hours ago";
        } else {
            return diff / (3600 * 24) + " days ago";
        }
    }
}
