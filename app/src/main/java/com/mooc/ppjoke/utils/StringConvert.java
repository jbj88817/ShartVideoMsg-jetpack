package com.mooc.ppjoke.utils;

public class StringConvert {
    public static String convertNum(int count) {
        if (count < 1000) {
            return String.valueOf(count);
        }
        return count / 1000 + "k";
    }
}
