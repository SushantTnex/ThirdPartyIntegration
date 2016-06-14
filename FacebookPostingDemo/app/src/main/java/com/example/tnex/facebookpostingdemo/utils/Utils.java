package com.example.tnex.facebookpostingdemo.utils;

import android.util.Log;

/**
 * Created by tnex on 8/6/16.
 */
public class Utils {

    private static final String TAG = "Utils";
    private static boolean showlog = true;

    public static void showVerboesLog(String tag, String message)
    {
        if (showlog)
        {
            Log.v(tag, message);
        }
    }
}
