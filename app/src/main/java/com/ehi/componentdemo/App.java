package com.ehi.componentdemo;

import android.app.Application;

import com.ehi.base.ModuleConfig;
import com.ehi.component.ComponentConfig;
import com.ehi.component.impl.EHiRxRouter;
import com.ehi.component.impl.application.EHiModuleManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化组件化相关
        ComponentConfig.init(this, BuildConfig.DEBUG);
        EHiRxRouter.tryErrorCatch();
        EHiModuleManager.getInstance().registerArr(
                ModuleConfig.App.NAME, ModuleConfig.Component1.NAME,
                ModuleConfig.Component2.NAME, ModuleConfig.User.NAME,
                ModuleConfig.Help.NAME
        );
        if (BuildConfig.DEBUG) {
            EHiModuleManager.getInstance().check();
        }
    }

}
