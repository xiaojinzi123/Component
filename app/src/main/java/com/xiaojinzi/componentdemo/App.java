package com.xiaojinzi.componentdemo;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.RxErrorIgnoreUtil;

public class App extends Application {

    @NonNull
    private static Application mApp;

    @NonNull
    public static Application getApp() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApp = this;

        // 初始化组件化相关
        Component.init(this, BuildConfig.DEBUG);
        // 打开 Gradle 初始化优化开关
        // Component.openInitOptimize();
        // 忽略一些不想处理的错误
        RxErrorIgnoreUtil.ignoreError();

        long startTime = System.currentTimeMillis();
        // 装载各个业务组件
        ModuleManager.getInstance().registerArr(
                ModuleConfig.App.NAME, ModuleConfig.Module1.NAME,
                ModuleConfig.Module2.NAME, ModuleConfig.Help.NAME,
                ModuleConfig.User.NAME, "base"
        );
        long endTime = System.currentTimeMillis();
        LogUtil.log("Componnet.Application", "---------------------------------耗时：" + (endTime - startTime));

        if (BuildConfig.DEBUG) {
            ModuleManager.getInstance().check();
        }

    }

}
