package com.ehi.component1;


import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.base.service.inter.component1.Component1Service;
import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.service.EHiServiceContainer;
import com.ehi.component.service.SingletonService;
import com.ehi.component1.service.Component1ServiceImpl;

@EHiModuleAppAnno()
public class Component1Application implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Context app) {
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
        EHiServiceContainer.unregister(Component1Service.class);
    }

}
