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

        ComponentConfig.init(this,BuildConfig.DEBUG);
        EHiRxRouter.tryErrorCatch();

        EHiModuleManager moduleManager = EHiModuleManager.getInstance();
        moduleManager.register(ModuleConfig.App.NAME);
        moduleManager.register(ModuleConfig.Component1.NAME);
        moduleManager.register(ModuleConfig.Component2.NAME);
        moduleManager.register(ModuleConfig.User.NAME);
        moduleManager.register(ModuleConfig.Help.NAME);

        if (BuildConfig.DEBUG) {
            moduleManager.check();
        }

    }
}
