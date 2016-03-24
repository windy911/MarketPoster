package com.rambo.marketposter.utils;

/*
 * Tools for String
 */
public class StringUtil {

    public static boolean isEmpty(String str) {
        if (str == null)
            return true;
        if (str.equals(""))
            return true;
        if (str.toLowerCase().equals("null"))
            return true;
        else
            return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    public static boolean rangeInDefined(int current, int min, int max) {
        return Math.max(min, current) == Math.min(current, max);
    }

    public static boolean isBlank(String str) {
        if (" ".equals(str)) return true;
        else return false;
    }
}
