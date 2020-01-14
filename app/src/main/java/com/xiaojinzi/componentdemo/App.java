package com.xiaojinzi.componentdemo;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.Config;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.RxErrorIgnoreUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化组件化相关
        Component.init(
                BuildConfig.DEBUG,
                Config.with(this)
                        .defaultScheme("router")
                        .optimizeInit(true)
                        .build()
        );
        // 关闭使用 Application 的日志, 可以不用调用, 提醒用户尽量避免使用 Application 进行跳转
        // Component.closeLogWhenUseApplication();
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

        LogUtil.log("Componnet", "---------------------------------耗时：" + (endTime - startTime));

        if (BuildConfig.DEBUG) {
            ModuleManager.getInstance().check();
        }

    }

}
