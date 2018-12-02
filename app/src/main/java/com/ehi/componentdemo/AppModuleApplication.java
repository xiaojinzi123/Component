package com.ehi.componentdemo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiModuleApp;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.impl.EHiRouter;

@EHiModuleApp()
public class AppModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull Context app) {
        EHiRouter.register(ModuleConfig.App.NAME);
    }

    @Override
    public void onDestory() {
        EHiRouter.unregister(ModuleConfig.App.NAME);
    }

}
