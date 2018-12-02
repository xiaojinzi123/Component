package com.ehi.componentdemo;

import android.app.Application;
import android.net.Uri;

import com.ehi.base.ModuleConfig;
import com.ehi.component.ComponentConfig;
import com.ehi.component.impl.EHiModuleManager;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.impl.EHiRouterExecuteResult;
import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.impl.EHiRouterResult;
import com.ehi.component.impl.EHiRxRouter;
import com.ehi.component.support.EHiRouterInterceptor;

import java.util.Random;

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
                    private Random r = new Random();

                    @Override
                    public EHiRouterExecuteResult intercept(Chain chain) throws Exception {
                        String name = Thread.currentThread().getName();
                        int seconds = r.nextInt(2) + 1;
                        Thread.sleep(1000 * seconds);
                        System.out.println("seconds = " + seconds + ",currentThreadName = " + name);
                        return chain.proceed(chain.request());
                        //return new EHiRouterExecuteResult(chain.request());
                    }
                });

    }
}
