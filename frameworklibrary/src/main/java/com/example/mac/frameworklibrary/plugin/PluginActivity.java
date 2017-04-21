package com.example.mac.frameworklibrary.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.mac.frameworklibrary.base.IPlugin;
import com.example.mac.frameworklibrary.utils.PluginConstants;
import com.example.mac.frameworklibrary.utils.PluginManager;
import com.example.mac.frameworklibrary.utils.PluginPackageInfo;


/**
 * Created by shake on 17-4-17.
 */
public abstract class PluginActivity extends Activity implements IPlugin {

    /**
     * 代理activity，可以当作Context来使用，会根据需要来决定是否指向this
     */
    protected Activity proxyActivity;

    /**
     * 等同于mProxyActivity，可以当作Context来使用，会根据需要来决定是否指向this,可以当作this来使用
     */
    protected Activity that;


    protected PluginPackageInfo mInfo;

    //默认是在本进程内的
    protected int mFrom = PluginConstants.FROM_INTERNAL;

    /**
     * TODO 。。。？
     */
    protected PluginManager mPluginManager;


    /**
     * 获取到代理对象的引用
     *
     * @param proxyActivity ---目的：为了回调资源
     * @param pluginPackage
     */
    @Override
    public void getProxyReference(Activity proxyActivity, PluginPackageInfo pluginPackage) {
        //获取到代理Activity的引用
        this.proxyActivity = proxyActivity;
        that = proxyActivity;
        mInfo = pluginPackage;
    }


    /**
     * 判断当前这个Activity是不是代理对象
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        //以下代码作用：判断当前这个Activity是主程序的Activity，还是插件程序的Activity
        if (savedInstanceState != null) {
            mFrom = savedInstanceState.getInt(PluginConstants.FROM, PluginConstants.FROM_INTERNAL);
        }

        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onCreate(savedInstanceState);
            //TODO 这里有点晕。。。把proxyActivity指向当前，即这里其实是插件进程
            proxyActivity = this;
            that = proxyActivity;
        }

        // TODO 这里为什么要用that，是不是因为 PluginManager 是属于宿主的？
        mPluginManager = PluginManager.getsInstance(that);

    }


    @Override
    public void setContentView(View view) {

        //如果是主程序，就调用主程序的方法
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.setContentView(view);
        } else {
            //如果你是插件程序，那么我就调用代理对象的的setContentView();
            //我将这个现实View的过程交给了代理对象
            proxyActivity.setContentView(view);
        }
    }

    /**
     * TODO 这个that究竟是什么含义
     *
     * @return
     */
    public Context getContext() {
        return that;
    }


    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.setContentView(view, params);
        } else {
            proxyActivity.setContentView(view, params);
        }
    }


    @Override
    public void setContentView(int layoutResID) {

        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.setContentView(layoutResID);
        } else {
            proxyActivity.setContentView(layoutResID);
        }

    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.addContentView(view, params);
        } else {
            proxyActivity.addContentView(view, params);
        }
    }


    @Override
    public View findViewById(int id) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.findViewById(id);
        } else {
            return proxyActivity.findViewById(id);
        }
    }


    @Override
    public Intent getIntent() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getIntent();
        } else {
            return proxyActivity.getIntent();
        }
    }


    @Override
    public ClassLoader getClassLoader() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getClassLoader();
        } else {
            return proxyActivity.getClassLoader();
        }
    }


    @Override
    public Resources getResources() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getResources();
        } else {
            return proxyActivity.getResources();
        }
    }


    @Override
    public String getPackageName() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getPackageName();
        } else {
            return proxyActivity.getPackageName();
        }
    }


    @Override
    public LayoutInflater getLayoutInflater() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getLayoutInflater();
        } else {
            return proxyActivity.getLayoutInflater();
        }
    }

    @Override
    public MenuInflater getMenuInflater() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getMenuInflater();
        } else {
            return proxyActivity.getMenuInflater();
        }
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getSharedPreferences(name, mode);
        } else {
            return proxyActivity.getSharedPreferences(name, mode);
        }
    }


    @Override
    public ApplicationInfo getApplicationInfo() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getApplicationInfo();
        } else {
            return proxyActivity.getApplicationInfo();
        }
    }


    @Override
    public WindowManager getWindowManager() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getWindowManager();
        } else {
            return proxyActivity.getWindowManager();
        }
    }

    @Override
    public Window getWindow() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getWindow();
        } else {
            return proxyActivity.getWindow();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.getSystemService(name);
        } else {
            return proxyActivity.getSystemService(name);
        }
    }


    @Override
    public void finish() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.finish();
        } else {
            proxyActivity.finish();
        }
    }


    @Override
    public void onBackPressed() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onBackPressed();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onStart() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onStart();
        }
    }


    @Override
    public void onRestart() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onRestart();
        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onRestoreInstanceState(savedInstanceState);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onSaveInstanceState(outState);
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onNewIntent(intent);
        }
    }


    @Override
    public void onResume() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onResume();
        }
    }


    @Override
    public void onPause() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onPause();
        }
    }


    @Override
    public void onStop() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onStop();
        }
    }


    @Override
    public void onDestroy() {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onDestroy();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.onTouchEvent(event);
        }
        return false;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.onKeyUp(keyCode, event);
        }
        return false;
    }


    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onWindowAttributesChanged(params);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            super.onWindowFocusChanged(hasFocus);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return super.onCreateOptionsMenu(menu);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mFrom == PluginConstants.FROM_INTERNAL) {
            return onOptionsItemSelected(item);
        }
        return false;
    }


}
