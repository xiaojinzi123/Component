package com.xiaojinzi.component.support;

import androidx.annotation.AnyThread;

import androidx.annotation.NonNull;
import android.util.Log;

import com.xiaojinzi.component.Component;

/**
 * 用于打印日志
 * time   : 2019/01/25
 *
 * @author : xiaojinzi
 */
public class LogUtil {

    public static final String TAG = "-------- Component --------";

    private LogUtil() {
    }

    @AnyThread
    public static void loge(@NonNull String message) {
        loge(TAG, message);
    }

    @AnyThread
    public static void loge(@NonNull String tag, @NonNull String message) {
        if (Component.isDebug()) {
            Log.e(tag, message);
        }
    }

    @AnyThread
    public static void logw(@NonNull String message) {
        logw(TAG, message);
    }

    @AnyThread
    public static void logw(@NonNull String tag, @NonNull String message) {
        if (Component.isDebug()) {
            Log.w(tag, message);
        }
    }

    @AnyThread
    public static void log(@NonNull String message) {
        log(TAG, message);
    }

    @AnyThread
    public static void log(@NonNull String tag, @NonNull String message) {
        if (Component.isDebug()) {
            Log.d(tag, message);
        }
    }

}
