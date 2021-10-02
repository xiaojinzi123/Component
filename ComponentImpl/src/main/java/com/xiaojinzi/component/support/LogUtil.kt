package com.xiaojinzi.component.support

import android.util.Log
import com.xiaojinzi.component.Component.isDebug
import androidx.annotation.AnyThread
import com.xiaojinzi.component.support.LogUtil

/**
 * 用于打印日志
 * time   : 2019/01/25
 *
 * @author : xiaojinzi
 */
object LogUtil {

    private const val TAG = "-------- Component --------"

    @AnyThread
    @JvmStatic
    fun loge(message: String) {
        loge(TAG, message)
    }

    @AnyThread
    @JvmStatic
    fun loge(tag: String, message: String) {
        if (isDebug) {
            Log.e(tag, message)
        }
    }

    @AnyThread
    @JvmStatic
    fun logw(message: String) {
        logw(TAG, message)
    }

    @AnyThread
    @JvmStatic
    fun logw(tag: String, message: String) {
        if (isDebug) {
            Log.w(tag, message)
        }
    }

    @AnyThread
    @JvmStatic
    fun log(message: String) {
        log(TAG, message)
    }

    @AnyThread
    @JvmStatic
    fun log(tag: String, message: String) {
        if (isDebug) {
            Log.d(tag, message)
        }
    }

}