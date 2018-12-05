package com.ehi.component1.interceptor;

import android.app.Application;
import android.content.Context;

import com.ehi.component.support.EHiRouterInterceptor;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class Component1Interceptor2 implements EHiRouterInterceptor {

    public Component1Interceptor2(Application application) {
    }

    @Override
    public void intercept(Chain chain) throws Exception {
        chain.proceed(chain.request());
    }

}
