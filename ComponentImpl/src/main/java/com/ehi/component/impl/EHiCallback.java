package com.ehi.component.impl;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.support.NavigationDisposable;

/**
 * 当路由完成的时候,回调这个接口
 * 所有的调用顺序整理：
 * 1.{@link #onSuccess(EHiRouterResult)} --> {@link #onEvent(EHiRouterResult, Throwable)}
 * 2.{@link #onError(Throwable)} --> {@link #onEvent(EHiRouterResult, Throwable)}
 * 3.被取消的时候：{@link #onCancel(EHiRouterRequest)}
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
     * @param originalRequest 原始请求
     * @param error           发生的错误
     */
    @MainThread
    void onError(@Nullable EHiRouterRequest originalRequest, @NonNull Throwable error);

    /**
     * @param originalRequest 原始请求,可能为 null
     * @param result          路由成功时候的返回对象
     * @param error           发生的错误
     */
    @MainThread
    void onEvent(@Nullable EHiRouterRequest originalRequest, @Nullable EHiRouterResult result, @Nullable Throwable error);

    /**
     * 当真正被取消的时候调用这个方法
     *
     * @param request 最原始的请求
     */
    @MainThread
    void onCancel(@NonNull EHiRouterRequest request);

}
