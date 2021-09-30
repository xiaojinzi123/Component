package com.xiaojinzi.component.impl

/**
 * 为了实现 [BiCallback] 用户的这个 Callback 的各个方法最多只能执行一次
 */
class BiCallbackWrap<T>(private val targetBiCallback: BiCallback<T>) : BiCallback<T> {

    /**
     * 标记是否结束
     */
    private var isEnd = false

    @Synchronized
    override fun onSuccess(result: RouterResult, t: T) {
        if (!isEnd) {
            targetBiCallback.onSuccess(result, t)
        }
        isEnd = true
    }

    @Synchronized
    override fun onCancel(originalRequest: RouterRequest?) {
        if (!isEnd) {
            targetBiCallback.onCancel(originalRequest!!)
        }
        isEnd = true
    }

    @Synchronized
    override fun onError(errorResult: RouterErrorResult) {
        if (!isEnd) {
            targetBiCallback.onError(errorResult)
        }
        isEnd = true
    }

}