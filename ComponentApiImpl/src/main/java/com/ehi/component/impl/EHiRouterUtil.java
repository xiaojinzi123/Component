package com.ehi.component.impl;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 工具类
 */
class EHiRouterUtil {

    /**
     * 路由的线程池
     */
    public static ExecutorService threadPool = Executors.newFixedThreadPool(5);

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
        postActionToMainThread(new Runnable() {
            @Override
            public void run() {
                if (callback == null) {
                    return;
                }
                if (error == null) {
                    return;
                }
                callback.onEvent(null, error);
                callback.onError(error);
            }
        });

    }

    public static void successCallback(@Nullable final EHiCallback callback,
                                       @NonNull final EHiRouterResult result) {
        postActionToMainThread(new Runnable() {
            @Override
            public void run() {
                if (callback == null) {
                    return;
                }
                if (result == null) {
                    return;
                }
                callback.onEvent(result, null);
                callback.onSuccess(result);
            }
        });

    }

}
