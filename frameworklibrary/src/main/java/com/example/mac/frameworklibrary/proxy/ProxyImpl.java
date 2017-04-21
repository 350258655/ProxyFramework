package com.example.mac.frameworklibrary.proxy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.mac.frameworklibrary.base.IAttachable;
import com.example.mac.frameworklibrary.base.IPlugin;
import com.example.mac.frameworklibrary.utils.PluginConfigs;
import com.example.mac.frameworklibrary.utils.PluginConstants;
import com.example.mac.frameworklibrary.utils.PluginManager;
import com.example.mac.frameworklibrary.utils.PluginPackageInfo;

import java.lang.reflect.Constructor;

/**
 * Created by shake on 17-4-17.
 */
public class ProxyImpl {

    // 代理对象的引用
    private Activity mProxyActivity;

    // 启动的Activity的全类名
    private String mClass;

    // 启动的插件的包名
    private String mPackageName;


    // PluginManager实例
    private PluginManager mPluginManager;

    // 插件APK的包信息
    private PluginPackageInfo mInfo;

    // 插件APK的AssetManager
    private AssetManager mAssetManager;

    // 插件APK的Resources
    private Resources mResources;

    // 插件Activity的信息
    private ActivityInfo mActivityInfo;

    private Resources.Theme mTheme;

    // 插件Activity对象的引用
    private IPlugin mIPluginActivity;


    public ProxyImpl(Activity proxyActivity) {
        mProxyActivity = proxyActivity;
    }


    public void onCreate(Intent intent) {

        /**
         * TODO 第一步、设置ClassLoader，这步的ClassLoader是随便选一个吗
         */
        intent.setExtrasClassLoader(PluginConfigs.sPluginClassLoader);


        /**
         * 第二步、从Intent中获取插件的包名和 插件Activity的全类名
         */
        //从Intent中获取包名和类名
        mPackageName = intent.getStringExtra(PluginConstants.EXTRA_PACKAGE);
        mClass = intent.getStringExtra(PluginConstants.EXTRA_CLASS);
        Log.i("TAG", "宿主ProxyImpl，启动插件的包名是：" + mPackageName + ",类名：" + mClass);


        /**
         * 第三步、根据插件包名，获取封装插件信息的 "PluginPackageInfo" 对象
         */
        mPluginManager = PluginManager.getsInstance(mProxyActivity);
        //获取插件的包信息
        mInfo = mPluginManager.getInfo(mPackageName);
        mAssetManager = mInfo.mAssetManager;
        mResources = mInfo.mResources;


        /**
         * 第四步、初始化以及处理 插件APK中Activity的基本信息
         */
        initActivityInfo();
        handleActivityInfo();


        /**
         * 第五步、去启动插件Activity
         */
        launchTargetActivity();

    }

    /**
     * 启动插件程序
     */
    private void launchTargetActivity() {

        try {

            /**
             * 第一步、创建出 插件Activity的实例。并且强制转换为 IPlugin类型
             */
            //创建插件Activity的Class对象
            Class<?> pluginActivityClass = getPluginClassLoader().loadClass(mClass);
            //创建插件Activity的构造器
            Constructor<?> pluginActivityConstructor = pluginActivityClass.getConstructor(new Class[]{});
            //这里不再具备Activity生命周期特性，相当于一个普通的bean类
            Object pluginActivityObj = pluginActivityConstructor.newInstance(new Object[]{});
            //插件APK的引用
            mIPluginActivity = (IPlugin) pluginActivityObj;


            /**
             * 第二步、在这里绑定，即将插件APK中的Activity对象的引用给代理Activity持有。
             * 然后在代理Activity中就可以通过 插件Activity的引用，对插件Activity进行操作
             */
            ((IAttachable) mProxyActivity).attach(mIPluginActivity, mPluginManager);


            /**
             * TODO 第三步、将代理对象传给插件程序。然后插件就可以通过代理对象的引用进行一些操作。。。这里不是很明白为什么要这样
             */
            mIPluginActivity.getProxyReference(mProxyActivity, mInfo);


            /**
             * 第四步、执行onCreate()方法的时候有一个参数，这里就是创建一个参数，然后表明是通过 别的进程 调用的Activity
             */
            Bundle bundle = new Bundle();
            //标注是从插件进来的
            bundle.putInt(PluginConstants.FROM, PluginConstants.FROM_EXTERNAL);


            /**
             * 第五步、执行插件APK的onCreate方法
             */
            mIPluginActivity.onCreate(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void handleActivityInfo() {

        if (mActivityInfo.theme > 0) {
            mProxyActivity.setTheme(mActivityInfo.theme);
        }

        Resources.Theme superTheme = mProxyActivity.getTheme();

        mTheme = mResources.newTheme();

        mTheme.setTo(superTheme);

        // Finals适配三星以及部分加载XML出现异常BUG
        try {
            mTheme.applyStyle(mActivityInfo.theme, true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 初始化插件APK中Activity的基本信息
     */
    private void initActivityInfo() {

        PackageInfo packageInfo = mInfo.mPackageInfo;

        if ((packageInfo.activities != null) && (packageInfo.activities.length > 0)) {

            if (mClass == null) {
                mClass = packageInfo.activities[0].name;
            }

            // 下面的关于主题的
            int defaultTheme = packageInfo.applicationInfo.theme;
            for (ActivityInfo info : packageInfo.activities) {

                if (info.name.equals(mClass)) {

                    mActivityInfo = info;
                    if (mActivityInfo.theme == 0) {
                        if (defaultTheme != 0) {
                            mActivityInfo.theme = defaultTheme;
                        } else {

                            if (Build.VERSION.SDK_INT >= 14) {
                                mActivityInfo.theme = android.R.style.Theme_DeviceDefault;
                            } else {
                                mActivityInfo.theme = android.R.style.Theme;
                            }

                        }

                    }

                }

            }

        }

    }


    /**
     * 获取插件APK的ClassLoader
     *
     * @return
     */
    public ClassLoader getPluginClassLoader() {
        return mInfo.mClassLoader;
    }


    /**
     * 获取插件APK的AssetsManager
     *
     * @return
     */
    public AssetManager getPluginAssets() {
        return mAssetManager;
    }

    /**
     * 获取插件APK的Resource对象
     *
     * @return
     */
    public Resources getPluginResources() {
        return mResources;
    }


    /**
     * 获取插件APK的Theme对象
     *
     * @return
     */
    public Resources.Theme getPluginTheme() {
        return mTheme;
    }

}
