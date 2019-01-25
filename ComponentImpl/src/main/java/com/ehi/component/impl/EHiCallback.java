package com.ehi.component.impl;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 当路由完成的时候,回调这个接口
 */
public interface EHiCallback {

    /**
     * 当路由成功的时候,回调
     *
     * @param result
     */
    @MainThread
    void onSuccess(@NonNull EHiRouterResult result);

    /**
     * 当路由错误的时候,回调
     *
     * @param error
     */
    @MainThread
    void onError(@NonNull Throwable error);

    /**
     * 错误或者成功都会回调,取消的时候不会回调
     *
     * @param result 需要判断是否为空
     * @param error
     */
    @MainThread
    void onEvent(@Nullable EHiRouterResult result, @Nullable Throwable error);

    /**
     * 被取消了
     */
    @MainThread
    void onCancel(@NonNull EHiRouterRequest request);

}
