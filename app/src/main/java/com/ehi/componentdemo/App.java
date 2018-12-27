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

        ComponentConfig.init(this,true);
        EHiRxRouter.tryErrorCatch();

        EHiModuleManager.getInstance().register(ModuleConfig.App.NAME);
        EHiModuleManager.getInstance().register(ModuleConfig.Component1.NAME);
        EHiModuleManager.getInstance().register(ModuleConfig.Component2.NAME);
        EHiModuleManager.getInstance().register(ModuleConfig.User.NAME);

    }
}
