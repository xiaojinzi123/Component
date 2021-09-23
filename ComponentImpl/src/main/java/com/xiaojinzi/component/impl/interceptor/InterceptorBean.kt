package com.xiaojinzi.component.impl.interceptor

import com.xiaojinzi.component.impl.RouterInterceptor

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
data class InterceptorBean(
        /**
         * 拦截器
         */
        val interceptor: RouterInterceptor,
        /**
         * 优先级
         */
        val priority: Int
)