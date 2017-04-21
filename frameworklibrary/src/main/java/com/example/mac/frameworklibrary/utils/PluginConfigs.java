package com.example.mac.frameworklibrary.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by shake on 17-4-17.
 */
public class PluginConfigs {


    public static ClassLoader sPluginClassLoader = PluginConstants.class.getClassLoader();


    /**
     * 记录拷贝so库的时间
     *
     * @param cxt
     * @param soName
     * @param time
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setSoLastModifiedTime(Context cxt, String soName, long time) {
        SharedPreferences prefs = cxt.getSharedPreferences(PluginConstants.PREFERENCE_NAME,
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        prefs.edit().putLong(soName, time).apply();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static long getSoLastModifiedTime(Context cxt, String soName) {
        SharedPreferences prefs = cxt.getSharedPreferences(PluginConstants.PREFERENCE_NAME,
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        return prefs.getLong(soName, 0);
    }

}
