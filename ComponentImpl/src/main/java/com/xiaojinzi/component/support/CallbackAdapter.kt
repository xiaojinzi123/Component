package com.xiaojinzi.component.support

import androidx.annotation.UiThread
import com.xiaojinzi.component.impl.Callback
import com.xiaojinzi.component.impl.RouterErrorResult
import com.xiaojinzi.component.impl.RouterRequest
import com.xiaojinzi.component.impl.RouterResult

/**
 * 接口的空实现
 */
open class CallbackAdapter : Callback {

    @UiThread
    override fun onSuccess(result: RouterResult) {
        // empty
    }

    @UiThread
    override fun onError(errorResult: RouterErrorResult) {
        // empty
    }

    @UiThread
    override fun onEvent(successResult: RouterResult?, errorResult: RouterErrorResult?) {
        // empty
    }

    @UiThread
    override fun onCancel(originalRequest: RouterRequest) {
        // empty
    }

}