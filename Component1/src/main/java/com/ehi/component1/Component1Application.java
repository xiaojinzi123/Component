package com.ehi.component1;


import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.api.application.IComponentApplication;
import com.ehi.api.anno.EHiModuleApp;
import com.ehi.api.impl.EHiUiRouter;
import com.ehi.base.ComponentEnum;
import com.ehi.base.EHiServiceContainer;
import com.ehi.base.service.component1.Component1Service;
import com.ehi.component1.service.Component1ServiceImpl;

@EHiModuleApp()
public class Component1Application implements IComponentApplication {

    @Override
    public void onCreate(@NonNull Application app) {
        EHiUiRouter.register(ComponentEnum.Component1.getModuleName());
        EHiServiceContainer.register(Component1Service.class,new Component1ServiceImpl());
    }

    @Override
    public void onDestory() {
        EHiUiRouter.unregister(ComponentEnum.Component1.getModuleName());
        EHiServiceContainer.unregister(Component1Service.class);
    }

}
