package com.ehi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

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
     * 当请求对象构建出来以后调用的
     *
     * @param callback
     */
    public static void cancelCallback(@NonNull final EHiRouterRequest request, @Nullable final EHiCallback callback) {
        if (isMainThread()) {
            cancelCallbackOnMainThread(request, callback);
        } else {
            postActionToMainThread(new Runnable() {
                @Override
                public void run() {
                    cancelCallbackOnMainThread(request, callback);
                }
            });
        }
    }

    /**
     * @param callback
     */
    private static void cancelCallbackOnMainThread(@NonNull EHiRouterRequest request, @Nullable final EHiCallback callback) {
        if (callback == null) {
            return;
        }
        callback.onCancel(request);
    }

    /**
     * 当请求对象构建出来以后调用的
     *
     * @param callback
     * @param error
     */
    public static void errorCallback(@Nullable final EHiCallback callback,
                                     @NonNull final Exception error) {
        if (isMainThread()) {
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

    /**
     * @param callback
     * @param error
     */
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

        if (isRequestUnavailabled(result.getRequest())) {
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

    private static boolean isRequestUnavailabled(@NonNull EHiRouterRequest originalRequest) {
        Context context = originalRequest.context;
        Fragment fragment = originalRequest.fragment;

        if (context != null && context instanceof Activity) {
            Activity activity = (Activity) context;
            if (isActivityUnavailabled(activity)) {
                return true;
            }
        }

        if (fragment != null) {
            if (fragment.isDetached()) {
                return true;
            }
            FragmentActivity activity = fragment.getActivity();
            if (activity == null) {
                return true;
            }
            if (isActivityUnavailabled(activity)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isActivityUnavailabled(@NonNull Activity activity) {
        boolean isUseful = true;
        if (activity.isFinishing()) {
            isUseful = false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                isUseful = false;
            }
        }
        return !isUseful;
    }


}
