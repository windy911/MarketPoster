package com.rambo.marketposter.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public static String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
                .format(new Date());
    }

    public static String getDouble(double d1) {
        DecimalFormat df = new DecimalFormat("######0.00");
        String result = df.format(d1);
//        if (result.endsWith(".00")) {
//            result = result.replace(".00", "");
//        }

//        if (result.contains(".") && result.endsWith("0")) {
//            result = result.substring(0, result.length() - 1);
//        }
        return result;
    }

}
