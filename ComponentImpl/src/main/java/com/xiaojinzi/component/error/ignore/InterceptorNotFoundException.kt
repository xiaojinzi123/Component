package com.xiaojinzi.component.error.ignore

import java.lang.RuntimeException

/**
 * time   : 2019/01/10
 *
 * @author : xiaojinzi
 */
class InterceptorNotFoundException(message: String? = null) : RuntimeException(message)