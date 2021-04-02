package com.xiaojinzi.component

import android.content.Intent
import com.xiaojinzi.component.bean.ActivityResult
import com.xiaojinzi.component.error.ignore.ActivityResultException
import com.xiaojinzi.component.impl.*
import com.xiaojinzi.component.impl.BiCallback.BiCallbackAdapter
import com.xiaojinzi.component.support.CallbackAdapter
import com.xiaojinzi.component.support.NavigationDisposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/**
 * 完成一个跳转的挂起函数
 */
@ExperimentalCoroutinesApi
suspend fun Call.await() {
    suspendCancellableCoroutine<Unit> { cot ->
        if (cot.isCompleted) {
            return@suspendCancellableCoroutine
        }
        val navigationDisposable: NavigationDisposable = navigate(object : CallbackAdapter() {
            override fun onSuccess(result: RouterResult) {
                super.onSuccess(result)
                if (!cot.isCompleted) {
                    cot.resume(Unit, null)
                }
            }

            override fun onError(errorResult: RouterErrorResult) {
                super.onError(errorResult)
                if (!cot.isCompleted) {
                    cot.resumeWithException(errorResult.error)
                }
            }

            override fun onCancel(originalRequest: RouterRequest) {
                super.onCancel(originalRequest)
                if (!cot.isCompleted) {
                    cot.cancel()
                }
            }
        })
        cot.invokeOnCancellation {
            navigationDisposable.cancel()
        }
    }
}

/**
 * 获取 [ActivityResult] 的一个挂起函数
 */
@ExperimentalCoroutinesApi
suspend fun Call.activityResultAwait(): ActivityResult {
    return suspendCancellableCoroutine { cot ->
        if (cot.isCompleted) {
            return@suspendCancellableCoroutine
        }
        val navigationDisposable: NavigationDisposable = navigateForResult(object : BiCallbackAdapter<ActivityResult>() {
            override fun onSuccess(result: RouterResult, activityResult: ActivityResult) {
                super.onSuccess(result, activityResult)
                if (!cot.isCompleted) {
                    cot.resume(activityResult) {}
                }
            }

            override fun onError(errorResult: RouterErrorResult) {
                super.onError(errorResult)
                if (!cot.isCompleted) {
                    cot.resumeWithException(errorResult.error)
                }
            }

            override fun onCancel(originalRequest: RouterRequest) {
                super.onCancel(originalRequest)
                if (!cot.isCompleted) {
                    cot.cancel()
                }
            }
        })
        cot.invokeOnCancellation {
            navigationDisposable.cancel()
        }
    }
}

/**
 * 获取 [Intent] 的一个挂起函数
 */
@ExperimentalCoroutinesApi
suspend fun Call.intentAwait(): Intent {
    return activityResultAwait().apply {
        if (this.data == null) {
            throw ActivityResultException("the intent result data is null")
        }
    }.data!!
}

/**
 * 获取 [Intent] 的一个挂起函数, 同时支持匹配 resultCode
 */
@ExperimentalCoroutinesApi
suspend fun Call.intentResultCodeMatchAwait(expectedResultCode: Int): Intent {
    return activityResultAwait().apply {
        if (this.data == null) {
            throw ActivityResultException("the intent result data is null")
        }
        if (expectedResultCode != this.resultCode) {
            throw ActivityResultException("the resultCode is not matching $expectedResultCode")
        }
    }.data!!
}

/**
 * 获取 ResultCode 的一个挂起函数
 */
@ExperimentalCoroutinesApi
suspend fun Call.resultCodeAwait(): Int {
    return activityResultAwait().resultCode
}

/**
 * 匹配 ResultCode 的一个挂起函数
 */
@ExperimentalCoroutinesApi
suspend fun Call.resultCodeMatchAwait(expectedResultCode: Int) {
    activityResultAwait().let {
        if (expectedResultCode != it.resultCode) {
            throw ActivityResultException("the resultCode is not matching $expectedResultCode")
        }
    }
}