package com.example.mac.frameworklibrary.utils;

/**
 * Created by shake on 17-4-17.
 * 常量类
 */
public class PluginConstants {

    public static final String FROM = "extra.from";


    //应用内部（宿主程序）
    public static final int FROM_INTERNAL = 0;

    //应用外部（插件程序）
    public static final int FROM_EXTERNAL = 1;

    public static final String EXTRA_DEX_PATH = "extra.dex.path";
    public static final String EXTRA_CLASS = "extra.class";
    public static final String EXTRA_PACKAGE = "extra.package";

    public static final String PREFERENCE_NAME = "dynamic_load_configs";


    /**
     * 这两个是什么，先留着
     */
    public static final String PROXY_ACTIVITY_VIEW_ACTION = "com.shake.plugin.proxy.activity.VIEW";
    public static final String PROXY_FRAGMENT_ACTIVITY_VIEW_ACTION = "com.shake.plugin.proxy.activity.VIEW";


    /**
     * CPU架构
     */
    public static final String CPU_ARMEABI = "armeabi";
    public static final String CPU_X86 = "x86";
    public static final String CPU_MIPS = "mips";





}
