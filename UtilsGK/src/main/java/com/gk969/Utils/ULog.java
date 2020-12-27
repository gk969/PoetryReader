package com.gk969.Utils;

import android.util.Log;

/**
 * Created by SongJian on 2018/1/5.
 */

public class ULog {
    public static void v(String tag, String msg) {
        Log.v(tag, msg);
    }
    
    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }
    
    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }
    
    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }
    
    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }
    
    public static void v(String tag, String msg, Object... args) {
        Log.v(tag, String.format(msg, args));
    }
    
    public static void d(String tag, String msg, Object... args) {
        Log.d(tag, String.format(msg, args));
    }
    
    public static void i(String tag, String msg, Object... args) {
        Log.i(tag, String.format(msg, args));
    }
    
    public static void w(String tag, String msg, Object... args) {
        Log.w(tag, String.format(msg, args));
    }
    
    public static void e(String tag, String msg, Object... args) {
        Log.e(tag, String.format(msg, args));
    }
    
    public static void stackTrace(String tag) {
        d(tag, Log.getStackTraceString(new Throwable()));
    }
}
