package com.xiaojinzi.component.impl

import android.app.Activity
import android.os.Build
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import com.xiaojinzi.component.error.RouterRuntimeException
import com.xiaojinzi.component.support.LogUtil
import com.xiaojinzi.component.support.OnRouterCancel
import com.xiaojinzi.component.support.RouterRequestHelp
import com.xiaojinzi.component.support.Utils
import java.lang.Exception

object RouterUtil {

    private const val TAG = Router.TAG

    /**
     * 当请求对象构建出来以后调用的
     */
    @AnyThread
    fun cancelCallback(request: RouterRequest?,
                       callback: OnRouterCancel?) {
        Utils.postActionToMainThreadAnyway { RouterUtil.cancelCallbackOnMainThread(request, callback) }
        RouterUtil.deliveryListener(null, null, request)
    }

    @UiThread
    private fun cancelCallbackOnMainThread(request: RouterRequest?,
                                           callback: OnRouterCancel?) {
        if (request == null) {
            LogUtil.log(TAG, "route canceled, request is null!")
        } else {
            LogUtil.log(TAG, "route canceled：" + request.uri.toString())
        }
        if (callback == null) {
            return
        }
        callback.onCancel(request)
    }

    @AnyThread
    fun errorCallback(callback: Callback?,
                      biCallback: BiCallback<*>?,
                      errorResult: RouterErrorResult) {
        Utils.postActionToMainThreadAnyway { errorCallbackOnMainThread(callback, biCallback, errorResult) }
        deliveryListener(null, errorResult, null)
    }

    @UiThread
    private fun errorCallbackOnMainThread(callback: Callback?,
                                          biCallback: BiCallback<*>?,
                                          errorResult: RouterErrorResult) {
        Utils.checkNullPointer(errorResult, "errorResult")
        if (errorResult.originalRequest == null) {
            LogUtil.log(TAG, "route fail：routerRequest has not been created, errorClass is " + Utils.getRealThrowable(errorResult.error).javaClass.simpleName + ":" + Utils.getRealMessage(errorResult.error))
        } else {
            LogUtil.log(TAG, "route fail：" + errorResult.originalRequest.uri.toString() + " and errorClass is " + Utils.getRealThrowable(errorResult.error).javaClass.simpleName + ",errorMsg is '" + Utils.getRealMessage(errorResult.error) + "'")
        }
        // 如果发起了一个路由但是现在已经 GG 了, 那就不执行了回调了
        if (errorResult.originalRequest != null && isRequestUnavailable(errorResult.originalRequest)) {
            return
        }
        // 执行 Request 中 的 errorCallback
        if (errorResult.originalRequest != null) {
            try {
                RouterRequestHelp.executeAfterErrorAction(errorResult.originalRequest)
            } catch (e: Exception) {
                throw RouterRuntimeException("afterErrorCallback or afterEventCallback can't throw any exception!", e)
            }
        }
        if (callback != null) {
            callback.onError(errorResult)
            callback.onEvent(null, errorResult)
        }
        biCallback?.onError(errorResult)
    }

    @AnyThread
    fun successCallback(callback: Callback?,
                        successResult: RouterResult) {
        Utils.postActionToMainThreadAnyway { successCallbackOnMainThread(callback, successResult) }
        deliveryListener(successResult, null, null)
    }

    @UiThread
    private fun successCallbackOnMainThread(callback: Callback?,
                                            result: RouterResult) {
        Utils.checkNullPointer(result, "result")
        LogUtil.log(TAG, "route success：" + result.originalRequest.uri.toString())
        // 如果请求的界面已经 GG 了
        if (isRequestUnavailable(result.originalRequest)) {
            return
        }
        // 执行 Request 中 的 afterCallback
        try {
            RouterRequestHelp.executeAfterAction(result.finalRequest)
        } catch (e: Exception) {
            throw RouterRuntimeException("afterJumpCallback or afterEventCallback can't throw any exception!", e)
        }
        if (callback != null) {
            callback.onSuccess(result)
            callback.onEvent(result, null)
        }
    }

    @AnyThread
    fun deliveryListener(successResult: RouterResult?,
                         errorResult: RouterErrorResult?,
                         cancelRequest: RouterRequest?) {
        Utils.postActionToMainThreadAnyway { deliveryListenerOnMainThread(successResult, errorResult, cancelRequest) }
    }

    @UiThread
    fun deliveryListenerOnMainThread(successResult: RouterResult?,
                                     errorResult: RouterErrorResult?,
                                     cancelRequest: RouterRequest?) {
        for (listener in Router.routerListeners) {
            try {
                if (successResult != null) {
                    listener.onSuccess(successResult)
                }
                if (errorResult != null) {
                    listener.onError(errorResult)
                }
                if (cancelRequest != null) {
                    listener.onCancel(cancelRequest)
                }
            } catch (ignore: Exception) {
                // do nothing
            }
        }
    }

    /**
     * 是否 Request 是不可用的, 其实是判断关联的界面是否已经 GG
     *
     * @param originalRequest 路由请求对象
     * @return Request 是不可用的
     */
    private fun isRequestUnavailable(originalRequest: RouterRequest): Boolean {
        val context = originalRequest.context
        val fragment = originalRequest.fragment
        val act = Utils.getActivityFromContext(context)
        if (act != null) {
            if (isActivityUnavailable(act)) {
                return true
            }
        }
        if (fragment != null) {
            if (fragment.isDetached) {
                return true
            }
            val activity = fragment.activity ?: return true
            if (isActivityUnavailable(activity)) {
                return true
            }
        }
        return false
    }

    /**
     * 是否 Activity 已经 GG
     *
     * @param activity [Activity]
     * @return Activity 已经 GG
     */
    private fun isActivityUnavailable(activity: Activity): Boolean {
        var isUseful = true
        if (activity.isFinishing) {
            isUseful = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) {
            isUseful = false
        }
        return !isUseful
    }

}