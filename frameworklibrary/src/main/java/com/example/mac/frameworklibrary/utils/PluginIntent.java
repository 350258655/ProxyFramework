package com.example.mac.frameworklibrary.utils;

import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by shake on 17-4-20.
 */
public class PluginIntent extends Intent {

    private String mPluginPackage;
    private String mPluginClass;

    public PluginIntent() {
        super();
    }

    public PluginIntent(String pluginPackage) {
        super();
        this.mPluginPackage = pluginPackage;
    }

    public PluginIntent(String pluginPackage, String pluginClass) {
        super();
        this.mPluginPackage = pluginPackage;
        this.mPluginClass = pluginClass;
    }

    public PluginIntent(String pluginPackage, Class<?> clazz) {
        super();
        this.mPluginPackage = pluginPackage;
        this.mPluginClass = clazz.getName();
    }


    public String getPluginPackage() {
        return mPluginPackage;
    }

    public void setPluginPackage(String pluginPackage) {
        mPluginPackage = pluginPackage;
    }

    public String getPluginClass() {
        return mPluginClass;
    }

    public void setPluginClass(String pluginClass) {
        mPluginClass = pluginClass;
    }

    public void setPluginClass(Class<?> clazz) {
        mPluginClass = clazz.getName();
    }

    @Override
    public Intent putExtra(String name, Parcelable value) {
        setupExtraClassLoader(value);
        return super.putExtra(name, value);
    }


    @Override
    public Intent putExtra(String name, Serializable value) {
        setupExtraClassLoader(value);
        return super.putExtra(name, value);
    }


    /**
     * 设置类加载器
     *
     * @param value
     */
    private void setupExtraClassLoader(Object value) {

        ClassLoader pluginLoader = value.getClass().getClassLoader();
        PluginConfigs.sPluginClassLoader = pluginLoader;
        setExtrasClassLoader(pluginLoader);
    }


}
