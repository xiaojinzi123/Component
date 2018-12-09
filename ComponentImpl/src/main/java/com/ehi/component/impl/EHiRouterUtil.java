package com.ehi.component.impl;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.support.EHiErrorRouterInterceptor;

/**
 * 路由的工具类,内部都是一些help方法
 */
class EHiRouterUtil {

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

    public static void errorCallback(@Nullable final EHiCallback callback,
                                     @NonNull final Exception error) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            errorCallbackOnMainThread(callback, error);
        } else {
            postActionToMainThread(new Runnable() {
                @Override
                public void run() {
                    errorCallbackOnMainThread(callback, error);
                }
            });
        }
    }

    private static void errorCallbackOnMainThread(@Nullable final EHiCallback callback,
                                                  @NonNull final Exception error) {
        if (callback == null) {
            return;
        }
        if (error == null) {
            return;
        }
        callback.onEvent(null, error);
        callback.onError(error);
    }

    public static void successCallback(@Nullable final EHiCallback callback,
                                       @NonNull final EHiRouterResult result) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            successCallbackOnMainThread(callback, result);
        } else {
            postActionToMainThread(new Runnable() {
                @Override
                public void run() {
                    successCallbackOnMainThread(callback, result);
                }
            });
        }
    }

    public static void successCallbackOnMainThread(@Nullable final EHiCallback callback,
                                                   @NonNull final EHiRouterResult result) {
        if (callback == null) {
            return;
        }
        if (result == null) {
            return;
        }
        callback.onEvent(result, null);
        callback.onSuccess(result);
    }

    public static void deliveryError(@NonNull Exception error) {

        for (EHiErrorRouterInterceptor interceptor : EHiRouter.errorRouterInterceptors) {
            try {
                interceptor.onRouterError(error);
            } catch (Exception ignore) {
                // do nothing
            }
        }

    }


}
