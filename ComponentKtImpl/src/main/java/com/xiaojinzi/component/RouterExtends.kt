package com.xiaojinzi.component

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.xiaojinzi.component.bean.ActivityResult
import com.xiaojinzi.component.error.ignore.ActivityResultException
import com.xiaojinzi.component.impl.*
import com.xiaojinzi.component.impl.BiCallback.BiCallbackAdapter
import com.xiaojinzi.component.support.CallbackAdapter
import com.xiaojinzi.component.support.NavigationDisposable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

inline fun Fragment.withHost(host: String): Navigator {
    return Router.with(this).host(host)
}

inline fun Fragment.withHostAndPath(hostAndPath: String): Navigator {
    return Router.with(this).hostAndPath(hostAndPath)
}

inline fun Context.withHost(host: String): Navigator {
    return Router.with(this).host(host)
}

inline fun Context.withHostAndPath(hostAndPath: String): Navigator {
    return Router.with(this).hostAndPath(hostAndPath)
}

fun Call.forward(
        cancelCallback: (originalRequest: RouterRequest?) -> Unit = {},
        errorCallback: (errorResult: RouterErrorResult) -> Unit = {},
        successCallback: (result: RouterResult) -> Unit = {}
) {
    this.forward(object : CallbackAdapter() {
        override fun onSuccess(result: RouterResult) {
            super.onSuccess(result)
            successCallback.invoke(result)
        }

        override fun onError(errorResult: RouterErrorResult) {
            super.onError(errorResult)
            errorCallback.invoke(errorResult)
        }

        override fun onCancel(originalRequest: RouterRequest?) {
            super.onCancel(originalRequest)
            cancelCallback.invoke(originalRequest)
        }
    })
}

fun Call.forwardForResultCodeMatch(expectedResultCode: Int = Activity.RESULT_OK, successCallback: (result: RouterResult) -> Unit) {
    this.forwardForResultCodeMatch(object : CallbackAdapter() {
        override fun onSuccess(result: RouterResult) {
            super.onSuccess(result)
            successCallback.invoke(result)
        }
    }, expectedResultCode = expectedResultCode)
}

fun Call.forwardForResult(
        routerResultCallback: (result: RouterResult) -> Unit = {},
        activityResultCallback: (t: ActivityResult) -> Unit
) {
    this.forwardForResult(object : BiCallbackAdapter<ActivityResult>() {
        override fun onSuccess(result: RouterResult, t: ActivityResult) {
            super.onSuccess(result, t)
            routerResultCallback.invoke(result)
            activityResultCallback.invoke(t)
        }
    })
}

fun Call.forwardForIntent(
        routerResultCallback: (result: RouterResult) -> Unit = {},
        activityResultCallback: (t: Intent) -> Unit
) {
    this.forwardForIntent(object : BiCallbackAdapter<Intent>() {
        override fun onSuccess(result: RouterResult, t: Intent) {
            super.onSuccess(result, t)
            routerResultCallback.invoke(result)
            activityResultCallback.invoke(t)
        }
    })
}

fun Call.forwardForIntentAndResultCodeMatch(
        expectedResultCode: Int = Activity.RESULT_OK,
        routerResultCallback: (result: RouterResult) -> Unit = {},
        activityResultCallback: (t: Intent) -> Unit
) {
    this.forwardForIntentAndResultCodeMatch(object : BiCallbackAdapter<Intent>() {
        override fun onSuccess(result: RouterResult, t: Intent) {
            super.onSuccess(result, t)
            routerResultCallback.invoke(result)
            activityResultCallback.invoke(t)
        }
    }, expectedResultCode = expectedResultCode)
}

fun Call.forwardForTargetIntent(
    routerResultCallback: (result: RouterResult) -> Unit = {},
    targetIntentCallback: (t: Intent) -> Unit,
) {
    this.forwardForTargetIntent(object : BiCallbackAdapter<Intent>() {
        override fun onSuccess(result: RouterResult, t: Intent) {
            super.onSuccess(result, t)
            routerResultCallback.invoke(result)
            targetIntentCallback.invoke(t)
        }
    })
}

/**
 * 完成一个跳转的挂起函数
 */
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
 * 获取目标 [Intent] 的一个挂起函数
 */
suspend fun Call.targetIntentAwait(): Intent {
    return suspendCancellableCoroutine { cot ->
        if (cot.isCompleted) {
            return@suspendCancellableCoroutine
        }
        val navigationDisposable: NavigationDisposable = navigateForTargetIntent(object : BiCallbackAdapter<Intent>() {
            override fun onSuccess(result: RouterResult, targetIntent: Intent) {
                super.onSuccess(result, targetIntent)
                if (!cot.isCompleted) {
                    cot.resume(targetIntent) {}
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
suspend fun Call.resultCodeAwait(): Int {
    return activityResultAwait().resultCode
}

/**
 * 匹配 ResultCode 的一个挂起函数
 */
suspend fun Call.resultCodeMatchAwait(expectedResultCode: Int) {
    activityResultAwait().let {
        if (expectedResultCode != it.resultCode) {
            throw ActivityResultException("the resultCode is not matching $expectedResultCode")
        }
    }
}