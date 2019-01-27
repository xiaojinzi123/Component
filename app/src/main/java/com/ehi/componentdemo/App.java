package com.ehi.componentdemo;

import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.base.ModuleConfig;
import com.ehi.component.ComponentConfig;
import com.ehi.component.impl.EHiRxRouter;
import com.ehi.component.impl.application.EHiModuleManager;

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
        ComponentConfig.init(this, BuildConfig.DEBUG);
        EHiRxRouter.tryErrorCatch();

        EHiModuleManager.getInstance().registerArr(
                ModuleConfig.App.NAME, ModuleConfig.Module1.NAME,
                ModuleConfig.Module2.NAME, ModuleConfig.User.NAME,
                ModuleConfig.Help.NAME
        );

        if (BuildConfig.DEBUG) {
            EHiModuleManager.getInstance().check();
        }

    }

}
