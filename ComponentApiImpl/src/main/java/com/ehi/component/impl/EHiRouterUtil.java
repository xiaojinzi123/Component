package com.ehi.component.impl;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 工具类
 */
class EHiRouterUtil {

    public static ExecutorService singleThread =  Executors.newSingleThreadExecutor();

    private static Handler h = new Handler(Looper.getMainLooper());

    public static void postActionToMainThread(@NonNull Runnable r){
        h.post(r);
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
