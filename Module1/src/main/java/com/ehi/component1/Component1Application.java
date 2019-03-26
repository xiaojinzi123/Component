package com.ehi.component1;


import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.component.anno.ModuleAppAnno;
import com.ehi.component.application.IComponentApplication;

@ModuleAppAnno()
public class Component1Application implements IComponentApplication {

    @NonNull
    private static Application mApp;

    @NonNull
    public static Application getApp() {
        return mApp;
    }

    @Override
    public void onCreate(@NonNull final Application app) {
        mApp = app;
        // 你可以做一些当前业务模块的一些初始化
    }

    @Override
    public void onDestory() {
        // 你可以销毁有关当前业务模块的东西
    }

}
