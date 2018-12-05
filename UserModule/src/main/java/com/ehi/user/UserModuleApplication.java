package com.ehi.user;


import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.base.service.inter.user.UserService;
import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.service.EHiServiceContainer;
import com.ehi.user.service.UserServiceImpl;

@EHiModuleAppAnno()
public class UserModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Context app) {
        EHiServiceContainer.register(UserService.class,new UserServiceImpl());
    }

    @Override
    public void onDestory() {
        EHiServiceContainer.unregister(UserService.class);
    }

}
