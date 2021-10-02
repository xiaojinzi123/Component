package com.xiaojinzi.component.support

import android.app.Activity
import com.xiaojinzi.component.Component.isDebug
import android.os.Looper
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Handler
import com.xiaojinzi.component.error.RouterRuntimeException
import java.lang.NullPointerException
import java.lang.RuntimeException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * 一个工具类
 * time   : 2019/01/25
 *
 * @author : xiaojinzi
 */
object Utils {

    val COUNTER = AtomicInteger(0)
    private const val STR_PARAMETER = "parameter '"
    private const val STR_CAN_NOT_BE_NULL = "' can't be null"
    private const val MAIN_THREAD_ERROR_MSG = "Component mainThreadCall method timeout, A deadlock was happened. see: https://github.com/xiaojinzi123/Component/issues/79"
    private const val MAIN_THREAD_TIME_OUT: Long = 3000

    // 单线程的线程池
    private val workPool = Executors.newSingleThreadExecutor()

    /**
     * 主线程的Handler
     */
    private val h = Handler(Looper.getMainLooper())

    /**
     * 在主线程延迟执行任务
     */
    @AnyThread
    @JvmStatic
    fun postDelayActionToMainThread(@UiThread r: Runnable, delayMillis: Long) {
        h.postDelayed(r, delayMillis)
    }

    @AnyThread
    @JvmStatic
    fun postActionToWorkThread(r: Runnable) {
        workPool.submit(r)
    }

    /**
     * 在主线程执行任务
     */
    @AnyThread
    @JvmStatic
    fun postActionToMainThread(r: Runnable) {
        if (isMainThread()) {
            r.run()
        } else {
            h.post(r)
        }
    }

    /**
     * 在主线程执行任务,和上面的方法唯一的区别就是一定是post过去的
     */
    @JvmStatic
    fun postActionToMainThreadAnyway(r: Runnable) {
        h.post(r)
    }

    /**
     * 是否是主线程
     */
    @JvmStatic
    fun isMainThread(): Boolean {
        return Thread.currentThread() == Looper.getMainLooper().thread
    }

    /**
     * Activity 是否被销毁了
     */
    @JvmStatic
    fun isActivityDestoryed(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.isFinishing || activity.isDestroyed
        } else {
            activity.isFinishing
        }
    }

    /**
     * 获取真实错误的信息
     *
     * @param throwable
     */
    @JvmStatic
    fun getRealMessage(throwable: Throwable): String? {
        var throwable = throwable
        while (throwable.cause != null) {
            throwable = throwable.cause!!
        }
        return throwable.message
    }

    /**
     * 获取真实的错误对象,有时候一个 [Throwable.getCause] 就是自己本身,下面的代码看上去是死循环了
     * 但是 [Throwable.getCause] 方法内部做了判断
     */
    @JvmStatic
    fun getRealThrowable(throwable: Throwable): Throwable {
        var throwable = throwable
        checkNullPointer(throwable)
        while (throwable.cause != null) {
            throwable = throwable.cause!!
        }
        return throwable
    }

    /**
     * 是否是由于某一个错误引起的
     */
    @JvmStatic
    fun isCauseBy(throwable: Throwable, clazz: Class<out Throwable>): Boolean {
        var throwable = throwable
        checkNullPointer(throwable)
        if (throwable.javaClass == clazz) {
            return true
        }
        while (throwable.cause != null) {
            throwable = throwable.cause!!
            if (throwable.javaClass == clazz) {
                return true
            }
        }
        return false
    }
    /**
     * 检查字符串是否为空
     */
    /**
     * 检查字符串是否为空
     */
    @JvmStatic
    @JvmOverloads
    fun checkStringNullPointer(value: String?,
                               parameterName: String,
                               desc: String? = null): String {
        if (value == null || value.isEmpty()) {
            throw NullPointerException(
                    STR_PARAMETER + parameterName + STR_CAN_NOT_BE_NULL + if (desc == null) "" else ",$desc"
            )
        }
        return value
    }

    /**
     * 检查对象是否为空
     */
    @JvmStatic
    fun <T> checkNullPointer(value: T?): T {
        if (isDebug && value == null) {
            throw NullPointerException()
        }
        return value!!
    }

    /**
     * 检查对象是否为空
     */
    @JvmStatic
    fun <T> checkNullPointer(value: T?, parameterName: String): T {
        if (isDebug && value == null) {
            throw NullPointerException(STR_PARAMETER + parameterName + STR_CAN_NOT_BE_NULL)
        }
        return value!!
    }

    /**
     * 检查对象是否为空,如果为空就在 debug 的时候崩溃,release 不崩溃但是路由失败
     */
    @JvmStatic
    fun <T> debugCheckNullPointer(value: T?, parameterName: String): T {
        if (isDebug && value == null) {
            throw NullPointerException(STR_PARAMETER + parameterName + STR_CAN_NOT_BE_NULL)
        }
        return value!!
    }

    /**
     * 是否内存过低
     */
    @JvmStatic
    fun isLowMemoryDevice(activityManager: ActivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activityManager.isLowRamDevice
        } else {
            true
        }
    }

    /**
     * 从一个 [Context] 中获取 [Activity]
     * 由于 [Context] 有可能是 [ContextWrapper] 包装的,所以一直要从 [ContextWrapper.getBaseContext]
     * 方法中获取 [Context] 并判断是否是 [Activity]
     *
     * @param context 上下文的参数
     * @return 如果 [Context] 最开始是 [Activity] 的话会返回一个 [Activity],否则返回 null
     */
    @JvmStatic
    fun getActivityFromContext(context: Context?): Activity? {
        if (context == null) {
            return null
        }
        var realActivity: Activity? = null
        if (context is Activity) {
            realActivity = context
        } else {
            // 最终结束的条件是 realContext = null 或者 realContext 不是一个 ContextWrapper
            var realContext = context
            while (realContext is ContextWrapper) {
                realContext = realContext.baseContext
                if (realContext is Activity) {
                    realActivity = realContext
                    break
                }
            }
        }
        return realActivity
    }

    @JvmStatic
    fun debugThrowException(e: RuntimeException) {
        if (isDebug) {
            throw e
        }
    }

    @JvmStatic
    fun checkMainThread() {
        if (!isMainThread()) {
            throw RouterRuntimeException("the thread is not main thread!")
        }
    }

}