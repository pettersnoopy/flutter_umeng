package com.startupcloud.umeng.flutter_umeng;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author luopeng
 * Created at 2019/6/27 21:16
 */
public class PreferenceUtils {
    public static void saveData(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flutter_umeng_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flutter_umeng_data", 0);
        return sharedPreferences.getString(key, "");
    }

    public static void clearData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flutter_umeng_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }
}
