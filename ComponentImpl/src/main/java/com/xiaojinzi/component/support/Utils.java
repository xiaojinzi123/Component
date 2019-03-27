package com.xiaojinzi.component.support;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.ComponentConfig;

/**
 * 一个工具类
 * time   : 2019/01/25
 *
 * @author : xiaojinzi 30212
 */
public class Utils {

    private static final String STR_PARAMETER = "parameter '";
    private static final String STR_CAN_NOT_BE_NULL = "' can't be null";

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

    /**
     * 获取真实的错误对象,有时候一个 {@link Throwable#cause} 就是自己本身,下面的代码看上去是死循环了
     * 但是 {@link Throwable#getCause()} 方法内部做了判断
     *
     * @param throwable
     * @return
     */
    public static Throwable getRealThrowable(@NonNull Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    /**
     * 检查字符串是否为空,如果为空就在 debug 的时候崩溃,release 不崩溃但是路由失败
     *
     * @param value
     * @param parameterName
     * @return
     */
    public static String checkStringNullPointer(@Nullable String value, @NonNull String parameterName) {
        if (ComponentConfig.isDebug() && (value == null || value.isEmpty())) {
            throw new NullPointerException(STR_PARAMETER + parameterName + STR_CAN_NOT_BE_NULL);
        }
        return value;
    }

    /**
     * 检查字符串是否为空,如果为空就在 debug 的时候崩溃,release 不崩溃但是路由失败
     *
     * @param value
     * @param parameterName
     * @param desc
     * @return
     */
    public static String checkStringNullPointer(@Nullable String value, @NonNull String parameterName, @Nullable String desc) {
        if (ComponentConfig.isDebug() && (value == null || value.isEmpty())) {
            throw new NullPointerException(STR_PARAMETER + parameterName + STR_CAN_NOT_BE_NULL + (desc == null ? "" : "," + desc));
        }
        return value;
    }

    /**
     * 检查对象是否为空,如果为空就在 debug 的时候崩溃,release 不崩溃但是路由失败
     *
     * @param value
     * @param parameterName
     * @param <T>
     * @return
     */
    public static <T> T checkNullPointer(@Nullable T value, @NonNull String parameterName) {
        if (ComponentConfig.isDebug() && value == null) {
            throw new NullPointerException(STR_PARAMETER + parameterName + STR_CAN_NOT_BE_NULL);
        }
        return value;
    }

    /**
     * 是否内存过低
     *
     * @param activityManager
     * @return
     */
    public static boolean isLowMemoryDevice(@NonNull ActivityManager activityManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return activityManager.isLowRamDevice();
        } else {
            return true;
        }
    }

    /**
     * 从一个 {@link Context} 中获取 {@link Activity}
     * 由于 {@link Context} 有可能是 {@link ContextWrapper} 包装的,所以一直要从 {@link ContextWrapper#getBaseContext()}
     * 方法中获取 {@link Context} 并判断是否是 {@link Activity}
     *
     * @param context 上下文的参数
     * @return 如果 {@link Context} 最开始是 {@link Activity} 的话会返回一个 {@link Activity},否则返回 null
     */
    @Nullable
    public static Activity getActivityFromContext(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        Activity realActivity = null;
        if (context instanceof Activity) {
            realActivity = (Activity) context;
        } else {
            // 最终结束的条件是 realContext = null 或者 realContext 不是一个 ContextWrapper
            Context realContext = context;
            while (realContext instanceof ContextWrapper) {
                realContext = ((ContextWrapper) realContext).getBaseContext();
                if (realContext instanceof Activity) {
                    realActivity = (Activity) realContext;
                    break;
                }
            }
        }
        return realActivity;
    }

}
