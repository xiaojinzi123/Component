package com.xiaojinzi.component2.view

import com.xiaojinzi.component.anno.InterceptorAnno
import com.xiaojinzi.component.impl.RouterInterceptor

@InterceptorAnno("component2Test")
class Component2TestInterceptor : RouterInterceptor {

    @Throws(Exception::class)
    override fun intercept(chain: RouterInterceptor.Chain) {
        chain.proceed(chain.request())
    }

}
