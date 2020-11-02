package com.xiaojinzi.component.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.error.RouterRuntimeException;
import com.xiaojinzi.component.error.RunTimeTimeoutException;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 一个工具类
 * time   : 2019/01/25
 *
 * @author : xiaojinzi
 */
public class Utils {

    public static final AtomicInteger COUNTER = new AtomicInteger(0);

    private static final String STR_PARAMETER = "parameter '";
    private static final String STR_CAN_NOT_BE_NULL = "' can't be null";
    private static final String MAIN_THREAD_ERROR_MSG = "Component mainThreadCall method timeout, A deadlock was happened. see: https://github.com/xiaojinzi123/Component/issues/79";
    private static final long MAIN_THREAD_TIME_OUT = 3000;

    private Utils() {
    }

    /**
     * 主线程的Handler
     */
    private static Handler h = new Handler(Looper.getMainLooper());

    /**
     * 在主线程延迟执行任务
     */
    @AnyThread
    public static void postDelayActionToMainThread(@NonNull @UiThread Runnable r, long delayMillis) {
        h.postDelayed(r, delayMillis);
    }

    /**
     * 在主线程执行任务
     */
    @AnyThread
    public static void postActionToMainThread(@NonNull Runnable r) {
        if (isMainThread()) {
            r.run();
        } else {
            h.post(r);
        }
    }

    /**
     * 在主线程执行任务,和上面的方法唯一的区别就是一定是post过去的
     */
    public static void postActionToMainThreadAnyway(@NonNull Runnable r) {
        h.post(r);
    }

    /**
     * 是否是主线程
     */
    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * Activity 是否被销毁了
     */
    public static boolean isActivityDestoryed(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isFinishing() || activity.isDestroyed();
        } else {
            return activity.isFinishing();
        }
    }

    /**
     * 获取真实错误的信息
     *
     * @param throwable
     */
    @Nullable
    public static String getRealMessage(@NonNull Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable.getMessage();
    }

    /**
     * 获取真实的错误对象,有时候一个 {@link Throwable#getCause()} 就是自己本身,下面的代码看上去是死循环了
     * 但是 {@link Throwable#getCause()} 方法内部做了判断
     */
    @Nullable
    public static Throwable getRealThrowable(@NonNull Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    /**
     * 是否是由于某一个错误引起的
     */
    public static boolean isCauseBy(@NonNull Throwable throwable, @NonNull Class<? extends Throwable> clazz) {
        if (throwable.getClass() == clazz) {
            return true;
        }
        while (throwable.getClass() != null) {
            throwable = throwable.getCause();
            if (throwable.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查字符串是否为空
     */
    public static String checkStringNullPointer(@Nullable String value, @NonNull String parameterName) {
        return checkStringNullPointer(value, parameterName, null);
    }

    /**
     * 检查字符串是否为空
     */
    public static String checkStringNullPointer(@Nullable String value,
                                                @NonNull String parameterName,
                                                @Nullable String desc) {
        if (value == null || value.isEmpty()) {
            throw new NullPointerException(
                    STR_PARAMETER + parameterName + STR_CAN_NOT_BE_NULL + (desc == null ? "" : "," + desc)
            );
        }
        return value;
    }

    /**
     * 检查对象是否为空
     */
    public static <T> T checkNullPointer(@Nullable T value) {
        if (Component.isDebug() && value == null) {
            throw new NullPointerException();
        }
        return value;
    }

    /**
     * 检查对象是否为空
     */
    public static <T> T checkNullPointer(@Nullable T value, @NonNull String parameterName) {
        if (Component.isDebug() && value == null) {
            throw new NullPointerException(STR_PARAMETER + parameterName + STR_CAN_NOT_BE_NULL);
        }
        return value;
    }

    /**
     * 检查对象是否为空,如果为空就在 debug 的时候崩溃,release 不崩溃但是路由失败
     */
    public static <T> T debugCheckNullPointer(@Nullable T value, @NonNull String parameterName) {
        if (Component.isDebug() && value == null) {
            throw new NullPointerException(STR_PARAMETER + parameterName + STR_CAN_NOT_BE_NULL);
        }
        return value;
    }

    /**
     * 是否内存过低
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

    public static void debugThrowException(@NonNull RuntimeException e) {
        if (Component.isDebug()) {
            throw e;
        }
    }

    public static void checkMainThread() {
        if (!isMainThread()) {
            throw new RouterRuntimeException("the thread is not main thread!");
        }
    }

    /**
     * 主线程去获取一个东东
     *
     * @param callable 回调对象, 可返回一个值
     * @param <T>      具体返回的值
     * @return 具体返回的值
     */
    @NonNull
    @AnyThread
    public static <T> T mainThreadCallable(@NonNull final Callable<T> callable) {
        return mainThreadCallNullable(new CallNullable<T>() {
            @Nullable
            @Override
            public T get() {
                return callable.get();
            }
        });
    }

    @Nullable
    @AnyThread
    public static <T> T mainThreadCallNullable(@NonNull final CallNullable<T> callable) {
        if (isMainThread()) {
            return callable.get();
        } else {
            final AtomicReference<Boolean> haveException = new AtomicReference<>();
            final AtomicReference<T> tAtomicReference = new AtomicReference<>();
            final AtomicReference<RuntimeException> exceptionAtomicReference = new AtomicReference<>();
            Utils.postActionToMainThreadAnyway(new Runnable() {
                @Override
                public void run() {
                    if (haveException.get() == null) {
                        try {
                            tAtomicReference.set(callable.get());
                            haveException.set(false);
                        } catch (RuntimeException e) {
                            exceptionAtomicReference.set(e);
                            haveException.set(true);
                        }
                    }
                }
            });
            // 当前的时间戳
            long currentTimeMillis = System.currentTimeMillis();
            while (haveException.get() == null) {
                // 如果超过了 3 秒, 认为是死锁了, 因为正常根本不可能等这么久
                if ((System.currentTimeMillis() - currentTimeMillis) > MAIN_THREAD_TIME_OUT) {
                    exceptionAtomicReference.set(new RunTimeTimeoutException(MAIN_THREAD_ERROR_MSG));
                    haveException.set(true);
                }
            }
            if (haveException.get().booleanValue()) {
                throw exceptionAtomicReference.get();
            } else {
                return tAtomicReference.get();
            }
        }
    }

    @AnyThread
    public static void mainThreadAction(@NonNull @UiThread final Action action) {
        if (isMainThread()) {
            action.run();
        } else {
            final AtomicReference<Object> resultAtomicReference = new AtomicReference<>();
            final AtomicReference<RuntimeException> exceptionAtomicReference = new AtomicReference<>();
            Utils.postActionToMainThreadAnyway(new Runnable() {
                @Override
                public void run() {
                    try {
                        action.run();
                        resultAtomicReference.set(0);
                    } catch (RuntimeException e) {
                        exceptionAtomicReference.set(e);
                    }
                }
            });
            // 当前的时间戳
            long currentTimeMillis = System.currentTimeMillis();
            // 线程空转.
            while (resultAtomicReference.get() == null && exceptionAtomicReference.get() == null) {
                // 如果超过了 3 秒, 认为是死锁了, 因为正常根本不可能等这么久
                if ((System.currentTimeMillis() - currentTimeMillis) > MAIN_THREAD_TIME_OUT) {
                    exceptionAtomicReference.set(new RunTimeTimeoutException(MAIN_THREAD_ERROR_MSG));
                }
            }
            if (exceptionAtomicReference.get() != null) {
                throw exceptionAtomicReference.get();
            }
        }
    }

}
