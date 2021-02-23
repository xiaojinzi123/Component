package com.xiaojinzi.component2

import com.xiaojinzi.component.anno.GlobalInterceptorAnno
import com.xiaojinzi.component.impl.RouterInterceptor

/**
 * 测试的 Kotlin 的拦截器
 */
@GlobalInterceptorAnno
open class Component2Interceptor : RouterInterceptor{
    override fun intercept(chain: RouterInterceptor.Chain) {
        chain.proceed(chain.request())
    }
}