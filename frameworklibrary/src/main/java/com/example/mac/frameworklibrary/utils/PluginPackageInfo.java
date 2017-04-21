package com.example.mac.frameworklibrary.utils;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

/**
 * Created by shake on 17-4-17.
 * 插件APK详细信息类
 */
public class PluginPackageInfo {

    /**
     * 包名
     */
    public String packageName;

    /**
     * 默认Activity，也就是入口Activity
     */
    public String defaultActivity;

    /**
     * 类加载器
     */
    public DexClassLoader mClassLoader;

    /**
     * assets目录资源管理器
     */
    public AssetManager mAssetManager;

    /**
     * 资源(图片、文字、布局等、)
     */
    public Resources mResources;

    /**
     * 应用程序包信息
     */
    public PackageInfo mPackageInfo;


    public PluginPackageInfo(DexClassLoader loader, Resources resources,
                             PackageInfo packageInfo) {


        this.packageName = packageInfo.packageName;
        this.mClassLoader = loader;
        this.mAssetManager = resources.getAssets();
        mResources = resources;
        mPackageInfo = packageInfo;

        defaultActivity = parseDefaultActivityName();
    }


    /**
     * 约定好的协议，把入口Activity放在AndroidManifest中的第一个
     *
     * @return
     */
    private final String parseDefaultActivityName() {

        if (mPackageInfo.activities != null && mPackageInfo.activities.length > 0) {
            return mPackageInfo.activities[0].name;
        }
        return "";
    }


}
