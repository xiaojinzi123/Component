package com.xiaojinzi.component1.runalone;

import android.app.Application;

import androidx.annotation.NonNull;

import com.xiaojinzi.component.anno.ModuleAppAnno;
import com.xiaojinzi.component.application.IComponentApplication;

@ModuleAppAnno
public class ComponentApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull Application app) {
    }

    @Override
    public void onDestory() {
    }

}
