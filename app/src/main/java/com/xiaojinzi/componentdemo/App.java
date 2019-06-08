package com.xiaojinzi.componentdemo;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.impl.application.ModuleManager;
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

        // 忽略一些不想处理的错误
        RxErrorIgnoreUtil.ignoreError();

        // 装载各个业务组件
        ModuleManager.getInstance().registerArr(
                ModuleConfig.App.NAME, ModuleConfig.Module1.NAME,
                ModuleConfig.Module2.NAME, ModuleConfig.User.NAME,
                ModuleConfig.Help.NAME
        );

        if (BuildConfig.DEBUG) {
            ModuleManager.getInstance().check();
        }

    }

}
