package com.ehi.component1.interceptor;

import com.ehi.component.anno.EHiGlobalInterceptorAnno;
import com.ehi.component.impl.EHiRouterInterceptor;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
@EHiGlobalInterceptorAnno(priority = 2)
public class Component1Interceptor1 implements EHiRouterInterceptor {

    public Component1Interceptor1() {
    }

    @Override
    public void intercept(Chain chain) throws Exception {
        chain.proceed(chain.request());
    }

}
