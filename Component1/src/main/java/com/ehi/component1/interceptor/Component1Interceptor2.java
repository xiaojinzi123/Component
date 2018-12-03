package com.ehi.component1.interceptor;

import com.ehi.component.impl.EHiRouterExecuteResult;
import com.ehi.component.support.EHiRouterInterceptor;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class Component1Interceptor2 implements EHiRouterInterceptor {

    @Override
    public EHiRouterExecuteResult intercept(Chain chain) throws Exception {
        return chain.proceed(chain.request());
    }

}
