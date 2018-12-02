package com.ehi.component3;


import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiModuleApp;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.impl.EHiRouter;

@EHiModuleApp()
public class Component3Application implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Context app) {
        // 注册路由和服务
        EHiRouter.register(ModuleConfig.Component3.NAME);
    }

    @Override
    public void onDestory() {
        // 反注册路由和服务
        EHiRouter.unregister(ModuleConfig.Component3.NAME);
    }

}
