package com.ehi.component1.interceptor;

import android.app.Application;

import com.ehi.component.anno.EHiGlobalInterceptorAnno;
import com.ehi.component.impl.EHiRouterInterceptor;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
@EHiGlobalInterceptorAnno(priority = 4)
public class Component1Interceptor2 implements EHiRouterInterceptor {

    public Component1Interceptor2(Application application) {
    }

    @Override
    public void intercept(Chain chain) throws Exception {
        chain.proceed(chain.request());
    }

}
