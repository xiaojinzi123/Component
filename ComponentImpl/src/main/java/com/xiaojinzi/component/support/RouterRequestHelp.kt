package com.xiaojinzi.component.support

import androidx.annotation.UiThread
import com.xiaojinzi.component.impl.RouterRequest

/**
 * 这是一个 [com.xiaojinzi.component.impl.RouterRequest] 对象的帮助类
 */
object RouterRequestHelp {

    @UiThread
    @Throws(Exception::class)
    fun executeBeforeAction(request: RouterRequest) {
        request.beforeAction?.invoke()
    }

    @UiThread
    @Throws(Exception::class)
    fun executeAfterAction(request: RouterRequest) {
        request.afterAction?.invoke()
        executeAfterEventAction(request)
    }

    @UiThread
    @Throws(Exception::class)
    fun executeAfterErrorAction(request: RouterRequest) {
        request.afterErrorAction?.invoke()
        executeAfterEventAction(request)
    }

    @UiThread
    @Throws(Exception::class)
    fun executeAfterEventAction(request: RouterRequest) {
        request.afterEventAction?.invoke()
    }

}