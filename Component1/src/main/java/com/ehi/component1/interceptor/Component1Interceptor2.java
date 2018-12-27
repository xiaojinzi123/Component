package com.ehi.component1.interceptor;

import android.app.Application;

import com.ehi.component.anno.EHiInterceptorAnno;
import com.ehi.component.impl.EHiRouterInterceptor;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
@EHiInterceptorAnno(priority = 4)
public class Component1Interceptor2 implements EHiRouterInterceptor {

    public Component1Interceptor2(Application application) {
        System.out.println("123123123");
    }

    @Override
    public void intercept(Chain chain) throws Exception {
        chain.proceed(chain.request());
    }

}
