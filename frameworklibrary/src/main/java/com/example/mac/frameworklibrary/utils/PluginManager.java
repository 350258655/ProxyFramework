package com.example.mac.frameworklibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.example.mac.frameworklibrary.plugin.PluginActivity;
import com.example.mac.frameworklibrary.proxy.ProxyActivity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by shake on 17-4-17.
 * 插件管理类
 */
public class PluginManager {


    // 成功StartActivity
    public static final int START_RESULT_SUCCESS = 0;

    public static final int START_RESULT_NO_PKG = 1;

    public static final int START_RESULT_NO_CLASS = 2;

    public static final int START_RESULT_TYPE_ERROR = 3;

    private static PluginManager sInstance;

    // 在我们的应用程序安装目录，创建存放插件的so目录
    private String mNativeLibDir = null;

    private Context mContext;


    // 默认值：默认是主程序
    // 标记当前APK程序是否是插件程序，或者是主程序
    private int mFrom = PluginConstants.FROM_INTERNAL;

    // 缓存插件包信息的集合
    private final HashMap<String, PluginPackageInfo> mPackageHolder = new HashMap<>();


    public PluginManager(Context context) {
        mContext = context.getApplicationContext();
        mNativeLibDir = mContext.getDir("pluginlib", Context.MODE_PRIVATE).getAbsolutePath();
    }


    public static PluginManager getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (PackageManager.class) {
                if (sInstance == null) {
                    sInstance = new PluginManager(context);
                }
            }
        }
        return sInstance;
    }


    /**
     * TODO 这是提供给宿主程序调用的，宿主程序调用这个方法，加载插件APK
     *
     * @param dexPath
     * @return
     */
    public PluginPackageInfo loadApk(String dexPath) {
        return loadApk(dexPath, true);
    }

    /**
     * 加载APK文件，说白了就是解析APK。最终获取一个我们封装的 "PluginPackageInfo" 类
     *
     * @param dexPath  －－－APK路径
     * @param hasSoLib －－－APK是否存在.so库
     * @return
     */
    private PluginPackageInfo loadApk(String dexPath, boolean hasSoLib) {

        mFrom = PluginConstants.FROM_EXTERNAL;


        /**
         * 第一步、根据获取插件的路径(插件的路径我们是放在SD卡中的)，获取插件的包信息
         */
        PackageInfo packageInfo = mContext.getPackageManager()
                .getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);

        //判断插件是否存在
        if (packageInfo == null) {
            return null;
        }


        /**
         * 第二步、获取插件的详细信息，封装成我们自己定义的bean类
         */

        PluginPackageInfo pluginPackageInfo = preparePluginEnv(packageInfo, dexPath);


        /**
         * 第三步、假如插件中有.so库，应该把.so库加载到内存中
         */
        if (hasSoLib) {
            // 拷贝so库到我们的应用程序的安装目录
            copySoLib(dexPath);
        }


        return pluginPackageInfo;
    }

    /**
     * 拷贝so库到安装目录
     *
     * @param dexPath
     */
    private void copySoLib(String dexPath) {
        SoLibManager.getSoLoader().copyPluginSolib(mContext, dexPath, mNativeLibDir);
    }


    /**
     * 获取插件相信信息 (例如：插件程序类加载器、Resource插件资源管理器、Assets外部资源管理器,包信息、.so库信息等等......)
     *
     * @param packageInfo ---包信息
     * @param dexPath     ---插件路径
     * @return
     */
    private PluginPackageInfo preparePluginEnv(PackageInfo packageInfo, String dexPath) {

        /**
         * 第一步、判断有没有加载过插件，如果已经加载了，就直接返回
         */
        PluginPackageInfo pluginInfo = mPackageHolder.get(packageInfo.packageName);
        if (pluginInfo != null) {
            return pluginInfo;
        }

        /**
         * 第二步、创建封装 "PluginPackageInfo" 需要的参数
         */
        //创建插件的类加载器
        DexClassLoader classLoader = createDexClassLoader(dexPath);
        //创建插件程序的AssetManager资源管理器
        AssetManager assetManager = createAssetManager(dexPath);
        //创建插件程序Resources管理器
        Resources resources = createResources(assetManager);


        /**
         * 第三步、根据上述参数，创建 "PluginPackageInfo" 对象
         */
        pluginInfo = new PluginPackageInfo(classLoader, resources, packageInfo);


        /**
         * 第四步、将创建出来的 "PluginPackageInfo" 缓存到一个集合中
         */
        mPackageHolder.put(packageInfo.packageName, pluginInfo);

        return pluginInfo;
    }

    /**
     * 创建插件程序Resources管理器
     *
     * @param assetManager
     * @return
     */
    private Resources createResources(AssetManager assetManager) {
        Resources superRes = mContext.getResources();
        Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        return resources;
    }

    /**
     * 创建插件程序的AssetManager资源管理器
     *
     * @param dexPath
     * @return
     */
    private AssetManager createAssetManager(String dexPath) {

        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, dexPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 创建类加载器
     *
     * @param dexPath
     * @return
     */
    private DexClassLoader createDexClassLoader(String dexPath) {

        File dexOutputDir = mContext.getDir("dex", Context.MODE_PRIVATE);
        DexClassLoader loader = new DexClassLoader(dexPath,
                dexOutputDir.getAbsolutePath(), mNativeLibDir, mContext.getClassLoader());

        return loader;
    }


    /**
     * 获取插件的包信息
     *
     * @param packageName
     * @return
     */
    public PluginPackageInfo getInfo(String packageName) {
        return mPackageHolder.get(packageName);
    }


    /**
     * TODO 启动插件程序，这也是提供给外部调用的
     *
     * @param context
     * @param intent
     * @return
     */
    public int startPluginActivity(Context context, PluginIntent intent) {
        return startPluginActivityForResult(context, intent, -1);
    }


    /**
     * 启动插件程序
     *
     * @param context
     * @param intent
     * @param requestCode
     * @return
     */
    private int startPluginActivityForResult(Context context, PluginIntent intent, int requestCode) {

        /**
         * 第一步、判断是不是插件程序，如果不是插件程序，就调用主程序的功能
         */
        if (mFrom == PluginConstants.FROM_INTERNAL) {

            intent.setClassName(context, intent.getPluginClass());
            //去启动Activity
            performStartActivityForResult(context, intent, requestCode);
            return PluginManager.START_RESULT_SUCCESS;
        }


        /**
         * 第二步、判断插件程序包名是否为空，为空抛异常
         */
        String pluginPackageName = intent.getPluginPackage();
        if (TextUtils.isEmpty(pluginPackageName)) {
            throw new NullPointerException("disallow null packageName.");
        }


        /**
         * 第三步、判断包信息 "PluginPackageInfo" 是否存在，不存在就不处理(因为前面做了缓存)
         */
        PluginPackageInfo info = mPackageHolder.get(pluginPackageName);
        if (info == null) {
            return START_RESULT_NO_PKG;
        }

        /**
         * 第四步、获取 插件Activity的全类名，并且通过反射加载class对象
         */
        //获取全类名
        String pluginActivityclassName = getPluginActivityFullPath(intent, info);
        // 根据插件全类名，通过反射机制，加载类对象(是否存在这个插件类)
        Class<?> pluginActivityClass = loadPluginClass(info.mClassLoader, pluginActivityclassName);
        if (pluginActivityClass == null) {
            return START_RESULT_NO_CLASS;
        }


        /**
         * 第五步、获取代理Activity的Class对象
         */
        Class<? extends Activity> proxyActivityClass = getProxyActivityClass(pluginActivityClass);
        if (proxyActivityClass == null) {
            return START_RESULT_TYPE_ERROR;
        }


        /**
         * 第六步、以下更新Intent数据(本来是启动插件的Activity，替换成了代理Activity，但是外面都是不知道的)
         * 并且将插件Activity的全类名和包名，都设置到intent中
         */
        intent.putExtra(PluginConstants.EXTRA_CLASS, pluginActivityclassName);
        intent.putExtra(PluginConstants.EXTRA_PACKAGE, pluginPackageName);
        intent.setClass(mContext, proxyActivityClass);


        /**
         * 第七步、启动代理的Activity，接下来就会启动 ProxyActivity
         */
        performStartActivityForResult(context, intent, requestCode);

        return START_RESULT_SUCCESS;
    }


    /**
     * 获取代理Activity的Class对象
     *
     * @param clazz
     * @return
     */
    private Class<? extends Activity> getProxyActivityClass(Class<?> clazz) {

        Class<? extends Activity> activityClass = null;

        if (PluginActivity.class.isAssignableFrom(clazz)) {
            activityClass = ProxyActivity.class;
        }

        return activityClass;
    }


    /**
     * 加载插件类对象
     *
     * @param classLoader
     * @param className
     * @return
     */
    private Class<?> loadPluginClass(ClassLoader classLoader, String className) {

        Class<?> clazz = null;

        try {
            clazz = Class.forName(className, true, classLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clazz;
    }

    /**
     * 获取插件Activity的全类名
     *
     * @param intent
     * @param info
     * @return
     */
    private String getPluginActivityFullPath(PluginIntent intent, PluginPackageInfo info) {
        String className = intent.getPluginClass();

        className = (className == null ? info.defaultActivity : className);
        if (className.startsWith(".")) {
            className = intent.getPluginPackage() + className;
        }
        return className;
    }

    /**
     * 启动Activity
     *
     * @param context
     * @param intent
     * @param requestCode
     */
    private void performStartActivityForResult(Context context, PluginIntent intent, int requestCode) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }


}
