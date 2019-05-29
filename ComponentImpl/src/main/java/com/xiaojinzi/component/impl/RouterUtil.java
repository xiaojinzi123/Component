package com.xiaojinzi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.Utils;

/**
 * 路由的工具类,内部都是一些help方法
 * 路由的一个帮助类,基本上用于分发事件
 *
 * @author xiaojinzi 30212
 */
class RouterUtil {

    private RouterUtil() {
    }

    /**
     * 当请求对象构建出来以后调用的
     *
     * @param callback
     */
    public static void cancelCallback(@Nullable final RouterRequest request, @Nullable final OnRouterCancel callback) {
        if (Utils.isMainThread()) {
            cancelCallbackOnMainThread(request, callback);
        } else {
            Utils.postActionToMainThreadAnyway(new Runnable() {
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
    private static void cancelCallbackOnMainThread(@Nullable RouterRequest request,
                                                   @Nullable final OnRouterCancel callback) {
        LogUtil.log(Router.TAG, "路由取消：" + request.uri.toString());
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
    public static void errorCallback(@Nullable final Callback callback,
                                     @NonNull final RouterErrorResult errorResult) {
        if (Utils.isMainThread()) {
            errorCallbackOnMainThread(callback, errorResult);
        } else {
            Utils.postActionToMainThreadAnyway(new Runnable() {
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
    private static void errorCallbackOnMainThread(@Nullable final Callback callback,
                                                  @NonNull final RouterErrorResult errorResult) {
        if (errorResult == null) {
            return;
        }
        if (errorResult.getOriginalRequest() == null) {
            LogUtil.log(Router.TAG, "路由失败：" + Utils.getRealThrowable(errorResult.getError()).getClass().getSimpleName() + ":" + Utils.getRealMessage(errorResult.getError()));
        } else {
            LogUtil.log(Router.TAG, "路由失败：" + errorResult.getOriginalRequest().uri.toString() + " and errorClass is " + Utils.getRealThrowable(errorResult.getError()).getClass().getSimpleName() + ",errorMsg is '" + Utils.getRealMessage(errorResult.getError()) + "'");
        }
        if (callback == null) {
            return;
        }
        callback.onError(errorResult);
        callback.onEvent(null, errorResult);
    }

    public static void successCallback(@Nullable final Callback callback,
                                       @NonNull final RouterResult successResult) {
        if (Utils.isMainThread()) {
            successCallbackOnMainThread(callback, successResult);
        } else {
            Utils.postActionToMainThreadAnyway(new Runnable() {
                @Override
                public void run() {
                    successCallbackOnMainThread(callback, successResult);
                }
            });
        }
        deliveryListener(successResult, null, null);
    }

    public static void successCallbackOnMainThread(@Nullable final Callback callback,
                                                   @NonNull final RouterResult result) {
        if (result == null) {
            return;
        }
        LogUtil.log(Router.TAG, "路由成功：" + result.getOriginalRequest().uri.toString());
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

    @AnyThread
    public static void deliveryListener(@Nullable final RouterResult successResult,
                                        @Nullable final RouterErrorResult errorResult,
                                        @Nullable final RouterRequest cancelRequest) {
        if (Utils.isMainThread()) {
            deliveryListenerOnMainThread(successResult, errorResult, cancelRequest);
        } else {
            Utils.postActionToMainThreadAnyway(new Runnable() {
                @Override
                public void run() {
                    deliveryListenerOnMainThread(successResult, errorResult, cancelRequest);
                }
            });
        }
    }

    public static void deliveryListenerOnMainThread(@Nullable final RouterResult successResult,
                                                    @Nullable final RouterErrorResult errorResult,
                                                    @Nullable final RouterRequest cancelRequest) {
        for (RouterListener listener : Router.routerListeners) {
            try {
                if (successResult != null) {
                    listener.onSuccess(successResult);
                }
                if (errorResult != null) {
                    listener.onError(errorResult);
                }
                if (cancelRequest != null) {
                    listener.onCancel(cancelRequest);
                }
            } catch (Exception ignore) {
                // do nothing
            }
        }
    }

    private static boolean isRequestUnavailabled(@NonNull RouterRequest originalRequest) {
        Context context = originalRequest.context;
        Fragment fragment = originalRequest.fragment;
        Activity act = Utils.getActivityFromContext(context);
        if (act != null) {
            if (isActivityUnavailabled(act)) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            isUseful = false;
        }
        return !isUseful;
    }

}
