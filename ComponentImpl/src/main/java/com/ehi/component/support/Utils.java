package com.ehi.component.support;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;

/**
 * time   : 2019/01/25
 *
 * @author : xiaojinzi 30212
 */
public class Utils {

    private Utils() {
    }

    /**
     * 主线程的Handler
     */
    private static Handler h = new Handler(Looper.getMainLooper());

    /**
     * 在主线程执行任务
     *
     * @param r
     */
    public static void postActionToMainThread(@NonNull Runnable r) {
        if (isMainThread()) {
            r.run();
        } else {
            h.post(r);
        }
    }

    /**
     * 在主线程执行任务,和上面的方法唯一的区别就是一定是post过去的
     *
     * @param r
     */
    public static void postActionToMainThreadAnyway(@NonNull Runnable r) {
        h.post(r);
    }

    /**
     * 是否是主线程
     *
     * @return
     */
    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * 获取真实错误的信息
     *
     * @param throwable
     * @return
     */
    public static String getRealMessage(@NonNull Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable.getMessage();
    }

    public static Throwable getRealThrowable(@NonNull Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    public static String checkStringNullPointer(String value, @NonNull String parameterName) {
        if (ComponentConfig.isDebug() && (value == null || value.isEmpty())) {
            throw new NullPointerException("parameter '" + parameterName + "' can't be null");
        }
        return value;
    }

    public static String checkStringNullPointer(String value, @NonNull String parameterName, @Nullable String desc) {
        if (ComponentConfig.isDebug() && (value == null || value.isEmpty())) {
            throw new NullPointerException("parameter '" + parameterName + "' can't be null" + (desc == null ? "" : "," + desc));
        }
        return value;
    }

    public static <T> T checkNullPointer(T value, @NonNull String parameterName) {
        if (ComponentConfig.isDebug() && value == null) {
            throw new NullPointerException("parameter '" + parameterName + "' can't be null");
        }
        return value;
    }

    public static boolean isLowMemoryDevice(ActivityManager activityManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return activityManager.isLowRamDevice();
        } else {
            return true;
        }
    }

}
