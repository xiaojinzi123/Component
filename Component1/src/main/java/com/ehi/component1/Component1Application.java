package com.ehi.component1;


import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.base.service.inter.component1.Component1Service;
import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.service.EHiService;
import com.ehi.component.service.SingletonService;
import com.ehi.component1.service.Component1ServiceImpl;

@EHiModuleAppAnno()
public class Component1Application implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Application app) {
    }

    @Override
    public void onDestory() {
    }

}
