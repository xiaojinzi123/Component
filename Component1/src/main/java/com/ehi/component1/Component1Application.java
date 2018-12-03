package com.ehi.component1;


import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.base.ModuleConfig;
import com.ehi.base.service.EHiServiceContainer;
import com.ehi.base.service.SingletonService;
import com.ehi.base.service.inter.component1.Component1Service;
import com.ehi.component.anno.EHiModuleApp;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component1.service.Component1ServiceImpl;

@EHiModuleApp()
public class Component1Application implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Context app) {
        // 注册路由和服务
        EHiRouter.register(ModuleConfig.Component1.NAME);
        // 这里注册一个单例的,并且懒加载的服务
        EHiServiceContainer.register(Component1Service.class, new SingletonService<Component1Service>() {
            @Override
            public Component1Service getRaw() {
                return new Component1ServiceImpl(app);
            }
        });
    }

    @Override
    public void onDestory() {
        // 反注册路由和服务
        EHiRouter.unregister(ModuleConfig.Component1.NAME);
        EHiServiceContainer.unregister(Component1Service.class);
    }

}
