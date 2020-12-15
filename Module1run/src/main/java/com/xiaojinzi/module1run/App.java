package com.xiaojinzi.module1run;

import android.app.Application;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.Config;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component.support.RxErrorIgnoreUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化组件化相关
        Component.init(
                BuildConfig.DEBUG, Config.with(this)
                        .build()
        );

        // 忽略一些不想处理的错误
        RxErrorIgnoreUtil.ignoreError();

        // 装载各个业务组件
        ModuleManager.getInstance().registerArr("module1App", ModuleConfig.Module1.NAME);

        Component.check();

    }

}
