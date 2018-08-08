package com.ehi.componentdemo;

import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.api.anno.EHiModuleApp;
import com.ehi.api.impl.EHiUiRouter;
import com.ehi.base.ComponentEnum;
import com.ehi.api.application.IComponentApplication;

@EHiModuleApp()
public class MainModuleApp implements IComponentApplication {

    @Override
    public void onCreate(@NonNull Application app) {
        EHiUiRouter.register(ComponentEnum.App.getModuleName());
    }

    @Override
    public void onDestory() {
        EHiUiRouter.unregister(ComponentEnum.App.getModuleName());
    }

}
