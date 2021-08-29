package com.xiaojinzi.component.error.ignore

import java.lang.RuntimeException

/**
 * 表示路由的过程中发生异常
 *
 * time   : 2018/11/03
 *
 * @author : xiaojinzi
 */
class NavigationFailException : RuntimeException {
    constructor(message: String? = null) : super(message) {}
    constructor(message: String? = null, cause: Throwable? = null) : super(message, cause) {}
    constructor(cause: Throwable? = null) : super(cause) {}
}