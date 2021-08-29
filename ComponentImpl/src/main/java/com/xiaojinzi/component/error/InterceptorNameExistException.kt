package com.xiaojinzi.component.error

import java.lang.RuntimeException

/**
 * 拦截器的名称已经存在的异常
 * time   : 2019/01/10
 *
 * @author : xiaojinzi
 */
class InterceptorNameExistException(message: String?) : RuntimeException(message)