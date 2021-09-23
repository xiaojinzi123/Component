package com.xiaojinzi.component.bean

import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.impl.RouterInterceptor

/**
 * [.stringInterceptor] 和 [.classInterceptor] 必须有一个是有值的
 */
@CheckClassNameAnno
class PageInterceptorBean constructor(
        /**
         * 优先级
         */
        val priority: Int,
        val stringInterceptor: String? = null,
        val classInterceptor: Class<out RouterInterceptor>? = null,
) {
    constructor(
            priority: Int,
            stringInterceptor: String? = null,
    ) : this(
            priority = priority,
            stringInterceptor = stringInterceptor,
            classInterceptor = null,
    )

    constructor(
            priority: Int,
            classInterceptor: Class<out RouterInterceptor>? = null,
    ) : this(
            priority = priority,
            stringInterceptor = null,
            classInterceptor = classInterceptor,
    )

}