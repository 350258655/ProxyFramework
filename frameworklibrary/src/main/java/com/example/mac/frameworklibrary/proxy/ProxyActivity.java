package com.example.mac.frameworklibrary.proxy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.example.mac.frameworklibrary.base.IAttachable;
import com.example.mac.frameworklibrary.base.IPlugin;
import com.example.mac.frameworklibrary.utils.PluginManager;


/**
 * Created by shake on 17-4-17.
 * 代理Activity,代理Activity需要持有插件Activity的引用。
 * 代理Activity的生命周期又系统控制，在代理Activity的生命周期去控制插件Activity的生命周期
 */
public class ProxyActivity extends Activity implements IAttachable {

    //持有插件对象的引用
    protected IPlugin mRemoteActivity;

    //代理对象的实现类
    private ProxyImpl mProxyImpl = new ProxyImpl(this);


    /**
     * 获取到插件Activity的引用
     *
     * @param plugin
     * @param manager
     */
    @Override
    public void attach(IPlugin plugin, PluginManager manager) {
        mRemoteActivity = plugin;
    }

    /**
     * 执行ProxyActivty的onCreate()方法的时候、就去执行实现类 ProxyImpl中的onCreate()方法
     * 然后在 ProxyImpl中的onCreate()方法 中去加载插件的信息，然后获取插件Activity的引用，并且通过
     * 调用 attach 方法 把插件Activity的引用 传入到本类中来
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProxyImpl.onCreate(getIntent());
    }


    /**
     * 获取资源一定要调用插件的资源管理器
     *
     * @return
     */
    @Override
    public AssetManager getAssets() {
        return mProxyImpl.getPluginAssets() == null ? super.getAssets() : mProxyImpl.getPluginAssets();
    }


    /**
     * 获取资源一定要调用插件的资源管理器(说白了就是调用目标对象的方法)
     *
     * @return
     */
    @Override
    public Resources getResources() {
        return mProxyImpl.getPluginResources() == null ? super.getResources() : mProxyImpl.getPluginResources();
    }


    @Override
    public Resources.Theme getTheme() {
        return mProxyImpl.getPluginTheme() == null ? super.getTheme() : mProxyImpl.getPluginTheme();
    }


    @Override
    public ClassLoader getClassLoader() {
        return mProxyImpl.getPluginClassLoader();
    }


    /**
     * 以下是Activity生命周期的方法
     * 插件程序Activity的生命周期由代理对象控制和访问
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mRemoteActivity.onActivityResult(requestCode, requestCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        mRemoteActivity.onStart();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        mRemoteActivity.onRestart();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        mRemoteActivity.onResume();
        super.onResume();
    }


    @Override
    protected void onPause() {
        mRemoteActivity.onPause();
        super.onPause();
    }


    @Override
    protected void onStop() {
        mRemoteActivity.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mRemoteActivity.onDestroy();
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mRemoteActivity.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRemoteActivity.onSaveInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        mRemoteActivity.onNewIntent(intent);
        super.onNewIntent(intent);
    }


    @Override
    public void onBackPressed() {
        mRemoteActivity.onBackPressed();
        super.onBackPressed();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return mRemoteActivity.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        return mRemoteActivity.onKeyUp(keyCode, event);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        mRemoteActivity.onWindowAttributesChanged(params);
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mRemoteActivity.onWindowFocusChanged(hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mRemoteActivity.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mRemoteActivity.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }


}
