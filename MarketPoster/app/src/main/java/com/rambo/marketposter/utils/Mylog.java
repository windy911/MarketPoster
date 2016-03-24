package com.rambo.marketposter.utils;

import android.util.Log;

import com.rambo.marketposter.application.Constants;

/**
 * Created by windy on 15/8/19.
 */
public class Mylog {

    public static void d(String tag, String info) {
        if(Constants.isDebug)
        Log.d(tag, info);
    }

    public static void i(String tag, String info) {
        if(Constants.isDebug)
        Log.i(tag, info);
    }

    public static void e(String tag, String info) {
        if(Constants.isDebug)
        Log.e(tag, info);
    }
}
