package com.xiaojinzi.component.error

import java.lang.RuntimeException

/**
 * 路由框架的运行时异常, 需要奔溃给用户看, 不能处理的
 */
class RouterRuntimeException : RuntimeException {
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}