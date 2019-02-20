package com.ehi.component.impl.interceptor;

import com.ehi.component.impl.EHiRouterInterceptor;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
public class EHiInterceptorBean {

    /**
     * 拦截器
     */
    public final EHiRouterInterceptor interceptor;

    /**
     * 优先级
     */
    public final int priority;

    public EHiInterceptorBean(EHiRouterInterceptor interceptor, int priority) {
        this.interceptor = interceptor;
        this.priority = priority;
    }

}
