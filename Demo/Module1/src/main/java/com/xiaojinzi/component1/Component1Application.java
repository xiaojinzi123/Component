package com.xiaojinzi.component1;


import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaojinzi.component.anno.ModuleAppAnno;
import com.xiaojinzi.component.application.IApplicationLifecycle;
import com.xiaojinzi.component.support.LogUtil;

@ModuleAppAnno()
public class Component1Application implements IApplicationLifecycle {

    @NonNull
    private static Application mApp;

    @NonNull
    public static Application getApp() {
        return mApp;
    }

    @Override
    public void onCreate(@NonNull final Application app) {
        mApp = app;
        /*LogUtil.loge("模块1休眠一下");
        // 你可以做一些当前业务模块的一些初始化
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }*/
    }

    @Override
    public void onDestroy() {
        // 你可以销毁有关当前业务模块的东西
    }

}
