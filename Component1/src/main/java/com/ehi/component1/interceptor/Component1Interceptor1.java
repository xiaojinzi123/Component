package com.ehi.component1.interceptor;

import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.component.support.EHiRouterInterceptor;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class Component1Interceptor1 implements EHiRouterInterceptor {

    public Component1Interceptor1() {
    }

    public void component1Interceptor1() {
    }

    @Override
    public void intercept(Chain chain) throws Exception {
        chain.proceed(chain.request());
    }

}
