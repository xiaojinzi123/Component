package com.ehi.component1.runalone;

import android.app.Application;

import com.ehi.base.ModuleConfig;
import com.ehi.component.ComponentConfig;
import com.ehi.component.impl.RxRouter;
import com.ehi.component.impl.application.ModuleManager;
import com.ehi.component1.BuildConfig;

/**
 * time   : 2019/01/21
 *
 * @author : xiaojinzi 30212
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
