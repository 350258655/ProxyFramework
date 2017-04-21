package com.example.mac.frameworklibrary.base;


import com.example.mac.frameworklibrary.utils.PluginManager;

/**
 * Created by shake on 17-4-17.
 * 专门用于定义将插件对象赋值给代理对象
 */
public interface IAttachable {

    public void attach(IPlugin plugin, PluginManager manager);

}
