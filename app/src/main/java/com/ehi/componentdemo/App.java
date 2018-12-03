package com.ehi.componentdemo;

import android.app.Application;

import com.ehi.base.ModuleConfig;
import com.ehi.component.ComponentConfig;
import com.ehi.component.impl.EHiModuleManager;
import com.ehi.component.impl.EHiRxRouter;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EHiModuleManager.init(this);
        ComponentConfig.init(true);
        EHiRxRouter.tryErrorCatch();

        EHiModuleManager.getInstance().register(ModuleConfig.Component1.NAME);
        EHiModuleManager.getInstance().register(ModuleConfig.Component2.NAME);
        EHiModuleManager.getInstance().register(ModuleConfig.User.NAME);

    }
}
