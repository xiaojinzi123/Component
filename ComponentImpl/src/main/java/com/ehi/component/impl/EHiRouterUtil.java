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

import com.ehi.component.support.LogUtil;
import com.ehi.component.support.Utils;

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
        deliveryListener(null, null, request);
    }

    /**
     * @param callback
     */
    private static void cancelCallbackOnMainThread(@NonNull EHiRouterRequest request,
                                                   @Nullable final EHiCallback callback) {
        LogUtil.log(EHiRouter.TAG, "路由取消：" + request.uri.toString());
        if (callback == null) {
            return;
        }
        callback.onCancel(request);
    }

    /**
     * 当请求对象构建出来以后调用的
     *
     * @param callback
     * @param errorResult
     */
    public static void errorCallback(@Nullable final EHiCallback callback,
                                     @NonNull final EHiRouterErrorResult errorResult) {
        if (isMainThread()) {
            errorCallbackOnMainThread(callback, errorResult);
        } else {
            postActionToMainThread(new Runnable() {
                @Override
                public void run() {
                    errorCallbackOnMainThread(callback, errorResult);
                }
            });
        }
        deliveryListener(null, errorResult, null);
    }

    /**
     * @param callback
     * @param errorResult
     */
    private static void errorCallbackOnMainThread(@Nullable final EHiCallback callback,
                                                  @NonNull final EHiRouterErrorResult errorResult) {
        if (errorResult == null) {
            return;
        }
        if (errorResult.getOriginalRequest() == null) {
            LogUtil.log(EHiRouter.TAG, "路由失败：" + Utils.getRealMessage(errorResult.getError()));
        } else {
            LogUtil.log(EHiRouter.TAG, "路由失败：" + errorResult.getOriginalRequest().uri.toString() + ",errorMsg is '" + Utils.getRealMessage(errorResult.getError()) + "'");
        }
        if (callback == null) {
            return;
        }
        callback.onError(errorResult);
        callback.onEvent(null, errorResult);
    }

    public static void successCallback(@Nullable final EHiCallback callback,
                                       @NonNull final EHiRouterResult successResult) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            successCallbackOnMainThread(callback, successResult);
        } else {
            postActionToMainThread(new Runnable() {
                @Override
                public void run() {
                    successCallbackOnMainThread(callback, successResult);
                }
            });
        }
        deliveryListener(successResult, null, null);
    }

    public static void successCallbackOnMainThread(@Nullable final EHiCallback callback,
                                                   @NonNull final EHiRouterResult result) {
        if (result == null) {
            return;
        }
        LogUtil.log(EHiRouter.TAG, "路由成功：" + result.getOriginalRequest().uri.toString());
        if (callback == null) {
            return;
        }
        // 如果请求的界面已经gg了
        if (isRequestUnavailabled(result.getOriginalRequest())) {
            return;
        }
        callback.onSuccess(result);
        callback.onEvent(result, null);
    }

    public static void deliveryListener(@Nullable final EHiRouterResult successResult,
                                        @Nullable final EHiRouterErrorResult errorResult,
                                        @Nullable final EHiRouterRequest cancelRequest) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            deliveryListenerOnMainThread(successResult, errorResult, cancelRequest);
        } else {
            postActionToMainThread(new Runnable() {
                @Override
                public void run() {
                    deliveryListenerOnMainThread(successResult, errorResult, cancelRequest);
                }
            });
        }
    }

    public static void deliveryListenerOnMainThread(@Nullable final EHiRouterResult successResult,
                                                    @Nullable final EHiRouterErrorResult errorResult,
                                                    @Nullable final EHiRouterRequest cancelRequest) {
        for (EHiRouterListener interceptor : EHiRouter.routerListeners) {
            try {
                if (successResult != null) {
                    interceptor.onSuccess(successResult);
                }
                if (errorResult != null) {
                    interceptor.onError(errorResult);
                }
                if (cancelRequest != null) {
                    interceptor.onCancel(cancelRequest);
                }
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
