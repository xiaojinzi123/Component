package com.xiaojinzi.component.bean

import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.support.CustomerIntentCall

/**
 * 和注解{@link RouterAnno}是--对应的
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi
 */
@CheckClassNameAnno
data class RouterBean(
        /**
         * 可能会生成文档之类的
         */
        var desc: String?,
        /**
         * 这个目标 Activity Class,可能为空, 因为可能标记在静态方法上
         */
        var targetClass: Class<*>? = null,
        /**
         * 页面拦截器
         */
        val pageInterceptors: MutableList<PageInterceptorBean> = mutableListOf(),
        /**
         * 用户自定义的 {@link android.content.Intent},
         * 当标记在静态方法上并且返回值是 {@link android.content.Intent} 的时候会有值
         */
        var customerIntentCall: CustomerIntentCall? = null,
) {
    constructor() : this(desc = null, targetClass = null)
}
