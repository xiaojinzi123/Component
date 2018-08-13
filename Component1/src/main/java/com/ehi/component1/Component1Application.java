package com.ehi.component1;


import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.base.ComponentEnum;
import com.ehi.base.EHiServiceContainer;
import com.ehi.base.service.component1.Component1Service;
import com.ehi.component.anno.EHiModuleApp;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.impl.EHiUiRouter;
import com.ehi.component1.service.Component1ServiceImpl;

@EHiModuleApp()
public class Component1Application implements IComponentApplication {

    @Override
    public void onCreate(@NonNull Context app) {
        // 注册路由和服务
        EHiUiRouter.register(ComponentEnum.Component1.getModuleName());
        EHiServiceContainer.register(Component1Service.class,new Component1ServiceImpl());
    }

    @Override
    public void onDestory() {
        // 反注册路由和服务
        EHiUiRouter.unregister(ComponentEnum.Component1.getModuleName());
        EHiServiceContainer.unregister(Component1Service.class);
    }

}
