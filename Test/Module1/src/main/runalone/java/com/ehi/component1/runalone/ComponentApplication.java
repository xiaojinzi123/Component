package com.xiaojinzi.component1.runalone;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaojinzi.component.anno.ModuleAppAnno;

@ModuleAppAnno
public class ComponentApplication implements IApplicationLifecycle {

    @Override
    public void onCreate(@NonNull Application app) {
    }

    @Override
    public void onDestory() {
    }

}
