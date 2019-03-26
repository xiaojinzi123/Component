package com.ehi.component1.runalone;

import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.component.anno.ModuleAppAnno;
import com.ehi.component.application.IComponentApplication;

@ModuleAppAnno
public class ComponentApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull Application app) {
    }

    @Override
    public void onDestory() {
    }

}
