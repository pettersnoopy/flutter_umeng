package com.startupcloud.umeng.flutter_umeng;

import android.util.Log;

/**
 * @author luopeng
 * Created at 2019/6/29 15:19
 */
public class LogUtils {
    public static void i(String tag, String content) {
        if (!BuildConfig.DEBUG) {
            return;
        }

        Log.i(tag, content);
    }

    public static void e(String tag, String content) {
        if (!BuildConfig.DEBUG) {
            return;
        }

        Log.e(tag, content);
    }
}
