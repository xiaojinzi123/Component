package com.xiaojinzi.componentdemo;

import android.app.Application;
import androidx.annotation.NonNull;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.Config;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.RxErrorIgnoreUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        long startTime = System.currentTimeMillis();
        // 初始化组件化相关
        Component.init(
                BuildConfig.DEBUG,
                Config.with(this)
                        .defaultScheme("router")
                        // 开启启动优化, 必须配套使用 Gradle 插件
                        .optimizeInit(true)
                        // 自动加载所有模块
                        .autoRegisterModule(true)
                        .build()
        );
        // 装载各个业务组件
        /*ModuleManager.getInstance().autoRegister();*/
        /*ModuleManager.getInstance().registerArr(
                ModuleConfig.App.NAME, ModuleConfig.Module1.NAME,
                ModuleConfig.Module2.NAME, ModuleConfig.Help.NAME,
                ModuleConfig.User.NAME, "base"
        );*/
        long endTime = System.currentTimeMillis();

        LogUtil.log("---------------------------------耗时：" + (endTime - startTime));

        // 如果你依赖了 rx 版本, 请加上这句配置. 忽略一些不想处理的错误
        // 如果不是 rx 的版本, 请忽略
        RxErrorIgnoreUtil.ignoreError();

        if (BuildConfig.DEBUG) {
            ModuleManager.getInstance().check();
        }

    }

}
