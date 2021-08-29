package com.xiaojinzi.component.error

import java.lang.RuntimeException

/**
 * 创建拦截器失败,这个错误不需要忽略,在 debug 的时候应该被抛出
 * time   : 2019/01/10
 *
 * @author : xiaojinzi
 */
class CreateInterceptorException(cause: Throwable?) : RuntimeException(cause)