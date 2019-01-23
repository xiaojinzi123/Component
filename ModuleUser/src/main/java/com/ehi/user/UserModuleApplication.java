package com.ehi.user;


import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;

@EHiModuleAppAnno()
public class UserModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Application app) {
    }

    @Override
    public void onDestory() {
    }

}
