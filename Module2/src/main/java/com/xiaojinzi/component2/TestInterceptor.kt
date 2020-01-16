package com.xiaojinzi.component2

import com.xiaojinzi.component.anno.InterceptorAnno
import com.xiaojinzi.component.impl.RouterInterceptor

@InterceptorAnno("testInterceptor")
class TestInterceptor : RouterInterceptor {

    @Throws(Exception::class)
    override fun intercept(chain: RouterInterceptor.Chain) {
        chain.proceed(chain.request())
    }

}