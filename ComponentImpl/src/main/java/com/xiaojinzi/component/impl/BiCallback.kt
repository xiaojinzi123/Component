package com.xiaojinzi.component.impl

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.support.Function
import com.xiaojinzi.component.support.OnRouterCancel
import com.xiaojinzi.component.support.OnRouterError
import com.xiaojinzi.component.support.Utils

/**
 * [Callback] 在这个基础上, 表示可以携带一个参数的回调
 * 当整个流程完成的时候,回调这个接口
 *
 *
 * 详细的请查看 [Callback]
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
interface BiCallback<T> : OnRouterCancel, OnRouterError {

    /**
     * 当路由成功的时候,回调
     *
     * @param result 路由成功的对象
     * @param t      返回的对象
     */
    @UiThread
    fun onSuccess(result: RouterResult, t: T)

    /**
     * 做一个转化
     *
     * @param <T> T 转化为 R
     * @param <R> T 转化为 R
    </R></T> */
    abstract class Map<T, R>(private val targetBiCallback: BiCallback<R>) : BiCallback<T>, Function<T, R> {

        override fun onSuccess(result: RouterResult, t: T) {
            try {
                targetBiCallback.onSuccess(result, apply(t = t))
            } catch (e: Exception) {
                targetBiCallback.onError(RouterErrorResult(e))
            }
        }

        override fun onCancel(originalRequest: RouterRequest?) {
            targetBiCallback.onCancel(originalRequest)
        }

        override fun onError(errorResult: RouterErrorResult) {
            targetBiCallback.onError(errorResult)
        }

    }

    /**
     * 空白实现类
     */
    open class BiCallbackAdapter<T> : BiCallback<T> {
        @CallSuper
        override fun onSuccess(result: RouterResult, t: T) {}
        @CallSuper
        override fun onError(errorResult: RouterErrorResult) {}
        @CallSuper
        override fun onCancel(originalRequest: RouterRequest?) {}
    }

}