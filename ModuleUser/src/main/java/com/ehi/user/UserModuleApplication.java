package com.ehi.user;


import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.component.anno.ModuleAppAnno;
import com.ehi.component.application.IComponentApplication;

@ModuleAppAnno()
public class UserModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Application app) {
    }

    @Override
    public void onDestory() {
    }

}
