package com.xiaojinzi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.xiaojinzi.component.error.RouterRuntimeException;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.RouterRequestHelp;
import com.xiaojinzi.component.support.Utils;

/**
 * 路由的工具类,内部都是一些help方法
 * 路由的一个帮助类,基本上用于分发事件
 *
 * @author xiaojinzi
 */
class RouterUtil {

    private RouterUtil() {
    }

    /**
     * 当请求对象构建出来以后调用的
     */
    @AnyThread
    public static void cancelCallback(@Nullable final RouterRequest request, @Nullable final OnRouterCancel callback) {
        Utils.postActionToMainThreadAnyway(new Runnable() {
            @Override
            public void run() {
                cancelCallbackOnMainThread(request, callback);
            }
        });
        deliveryListener(null, null, request);
    }


    @MainThread
    private static void cancelCallbackOnMainThread(@Nullable RouterRequest request,
                                                   @Nullable final OnRouterCancel callback) {
        LogUtil.log(Router.TAG, "route canceled：" + request.uri.toString());
        if (callback == null) {
            return;
        }
        callback.onCancel(request);
    }

    @AnyThread
    public static void errorCallback(@Nullable final Callback callback, @Nullable final BiCallback biCallback,
                                     @NonNull final RouterErrorResult errorResult) {
        Utils.postActionToMainThreadAnyway(new Runnable() {
            @Override
            public void run() {
                errorCallbackOnMainThread(callback, biCallback, errorResult);
            }
        });
        deliveryListener(null, errorResult, null);
    }

    @MainThread
    private static void errorCallbackOnMainThread(@Nullable final Callback callback, @Nullable final BiCallback biCallback,
                                                  @NonNull final RouterErrorResult errorResult) {
        Utils.checkNullPointer(errorResult, "errorResult");
        if (errorResult.getOriginalRequest() == null) {
            LogUtil.log(Router.TAG, "route fail：routerRequest has not been created, errorClass is " + Utils.getRealThrowable(errorResult.getError()).getClass().getSimpleName() + ":" + Utils.getRealMessage(errorResult.getError()));
        } else {
            LogUtil.log(Router.TAG, "route fail：" + errorResult.getOriginalRequest().uri.toString() + " and errorClass is " + Utils.getRealThrowable(errorResult.getError()).getClass().getSimpleName() + ",errorMsg is '" + Utils.getRealMessage(errorResult.getError()) + "'");
        }
        // 如果发起了一个路由但是现在已经 GG 了, 那就不执行了回调了
        if (errorResult.getOriginalRequest() != null && isRequestUnavailabled(errorResult.getOriginalRequest())) {
            return;
        }
        // 执行 Request 中 的 errorCallback
        if (errorResult.getOriginalRequest() != null) {
            try {
                RouterRequestHelp.executeAfterErrorCallback(errorResult.getOriginalRequest());
            } catch (Exception e) {
                throw new RouterRuntimeException("afterErrorCallback or afterEventCallback can't throw any exception!", e);
            }
        }
        if (callback != null) {
            callback.onError(errorResult);
            callback.onEvent(null, errorResult);
        }
        if (biCallback != null) {
            biCallback.onError(errorResult);
        }
    }

    @AnyThread
    public static void successCallback(@Nullable final Callback callback,
                                       @NonNull final RouterResult successResult) {
        Utils.postActionToMainThreadAnyway(new Runnable() {
            @Override
            public void run() {
                successCallbackOnMainThread(callback, successResult);
            }
        });
        deliveryListener(successResult, null, null);
    }

    @MainThread
    private static void successCallbackOnMainThread(@Nullable final Callback callback,
                                                    @NonNull final RouterResult result) {
        Utils.checkNullPointer(result, "result");
        LogUtil.log(Router.TAG, "route success：" + result.getOriginalRequest().uri.toString());
        // 如果请求的界面已经gg了
        if (isRequestUnavailabled(result.getOriginalRequest())) {
            return;
        }
        // 执行 Request 中 的 afterCallback
        try {
            RouterRequestHelp.executeAfterJumpCallback(result.getOriginalRequest());
        } catch (Exception e) {
            throw new RouterRuntimeException("afterJumpCallback or afterEventCallback can't throw any exception!", e);
        }
        if (callback != null) {
            callback.onSuccess(result);
            callback.onEvent(result, null);
        }
    }

    @AnyThread
    public static void deliveryListener(@Nullable final RouterResult successResult,
                                        @Nullable final RouterErrorResult errorResult,
                                        @Nullable final RouterRequest cancelRequest) {
        Utils.postActionToMainThread(new Runnable() {
            @Override
            public void run() {
                deliveryListenerOnMainThread(successResult, errorResult, cancelRequest);
            }
        });
    }

    @MainThread
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

    /**
     * 是否 Request 是不可用的, 其实是判断关联的界面是否已经 GG
     *
     * @param originalRequest 路由请求对象
     * @return Request 是不可用的
     */
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

    /**
     * 是否 Activity 已经 GG
     *
     * @param activity {@link Activity}
     * @return Activity 已经 GG
     */
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
