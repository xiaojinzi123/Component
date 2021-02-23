package com.xiaojinzi.component1.runalone;

import android.app.Application;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.ComponentConfig;
import com.xiaojinzi.component.impl.RxRouter;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component1.BuildConfig;

/**
 * time   : 2019/01/21
 *
 * @author : xiaojinzi
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ComponentConfig.init(this, BuildConfig.DEBUG);
        RxRouter.tryErrorCatch();

        ModuleManager moduleManager = ModuleManager.getInstance();
        moduleManager.register(ModuleConfig.Component1.NAME);

        if (BuildConfig.DEBUG) {
            moduleManager.check();
        }

    }

}
