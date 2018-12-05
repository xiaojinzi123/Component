package com.ehi.user;


import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.base.service.inter.user.UserService;
import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.service.EHiService;
import com.ehi.user.service.UserServiceImpl;

@EHiModuleAppAnno()
public class UserModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Application app) {
    }

    @Override
    public void onDestory() {
    }

}
