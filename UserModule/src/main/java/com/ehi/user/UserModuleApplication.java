package com.ehi.user;


import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.base.ModuleConfig;
import com.ehi.base.service.EHiServiceContainer;
import com.ehi.base.service.inter.user.UserService;
import com.ehi.component.anno.EHiModuleApp;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.impl.EHiRouter;
import com.ehi.user.service.UserServiceImpl;

@EHiModuleApp()
public class UserModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Context app) {
        // 注册路由和服务
        EHiRouter.register(ModuleConfig.User.NAME);
        EHiServiceContainer.register(UserService.class,new UserServiceImpl());
    }

    @Override
    public void onDestory() {
        // 反注册路由和服务
        EHiRouter.unregister(ModuleConfig.User.NAME);
        EHiServiceContainer.unregister(UserService.class);
    }

}
