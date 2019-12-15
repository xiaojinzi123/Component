package com.xiaojinzi.component.impl.interceptor;

import com.xiaojinzi.component.impl.RouterInterceptor;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
public class InterceptorBean {

    /**
     * 拦截器
     */
    public final RouterInterceptor interceptor;

    /**
     * 优先级
     */
    public final int priority;

    public InterceptorBean(RouterInterceptor interceptor, int priority) {
        this.interceptor = interceptor;
        this.priority = priority;
    }

}
