package com.example.mac.frameworklibrary.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.mac.frameworklibrary.utils.PluginPackageInfo;


/**
 * Created by shake on 17-4-17.
 * 这里定义的都是Activity生命周期方法相关的
 */
public interface IPlugin {

    /**
     * 插件对象去持有代理对象(和前面相反，前面是代理对象持有插件对象引用 )
     *
     * @param proxyActivity
     *            ---目的：为了回调资源
     * @param pluginPackage
     *            ---将插件包信息传递进来，做一些相关逻辑处理 插件包信息的解析是在主程序当中执行
     */
    public void getProxyReference(Activity proxyActivity, PluginPackageInfo pluginPackage);

    /**
     * 目标接口的创建
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState);

    public void onStart();

    public void onRestart();

    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public void onResume();

    public void onPause();

    public void onStop();

    public void onDestroy();

    public void onSaveInstanceState(Bundle outState);

    public void onNewIntent(Intent intent);

    public void onRestoreInstanceState(Bundle savedInstanceState);

    public boolean onTouchEvent(MotionEvent event);

    public boolean onKeyUp(int keyCode, KeyEvent event);

    public void onWindowAttributesChanged(WindowManager.LayoutParams params);

    public void onWindowFocusChanged(boolean hasFocus);

    public void onBackPressed();

    public boolean onCreateOptionsMenu(Menu menu);

    public boolean onOptionsItemSelected(MenuItem item);

    void setContentView(View view, WindowManager.LayoutParams params);

    void addContentView(View view, WindowManager.LayoutParams params);
}
