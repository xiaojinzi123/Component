package com.ehi.componentdemo;

import android.app.Application;
import android.net.Uri;

import com.ehi.base.ModuleConfig;
import com.ehi.component.ComponentConfig;
import com.ehi.component.impl.EHiModuleManager;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.impl.EHiRouterResult;
import com.ehi.component.impl.EHiRxRouter;
import com.ehi.component.support.EHiRouterInterceptor;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EHiModuleManager.init(this);
        ComponentConfig.init(true);
        EHiRxRouter.tryErrorCatch();

        EHiModuleManager.getInstance().register(ModuleConfig.Component3.NAME);

        EHiRouter
                .addRouterInterceptor(new EHiRouterInterceptor() {
                    @Override
                    public EHiRouterResult intercept(Chain chain) throws Exception {
                        return chain.proceed(
                                new EHiRouterRequest.Builder()
                                        .context(chain.request().context)
                                        .fragment(chain.request().fragment)
                                        .uri(Uri.parse("EHi://component1/main"))
                                        .build()
                        );
                    }
                });

    }
}
