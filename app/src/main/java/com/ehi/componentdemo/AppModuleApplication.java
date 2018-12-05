package com.ehi.componentdemo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;

@EHiModuleAppAnno()
public class AppModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull Context app) {
    }

    @Override
    public void onDestory() {
    }

}
