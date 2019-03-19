package com.ehi.component.impl;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.support.NavigationDisposable;

/**
 * 当路由完成的时候,回调这个接口,这时候的完成不一定是成功的,可能是失败的,成功和失败都表示完成
 * 所有的调用顺序整理：
 * 1.{@link #onSuccess(EHiRouterResult)} --> {@link #onEvent(EHiRouterResult, EHiRouterErrorResult)}
 * 2.{@link #onError(EHiRouterErrorResult)} --> {@link #onEvent(EHiRouterResult, EHiRouterErrorResult)}
 * 3.被取消的时候：{@link #onCancel(EHiRouterRequest)}
 *
 * @author xiaojinzi 30212
 */
public interface EHiCallback {

    /**
     * 当路由成功的时候,回调
     *
     * @param result 路由成功的对象
     */
    @MainThread
    void onSuccess(@NonNull EHiRouterResult result);

    /**
     * 当路由错误的时候回调
     *
     * @param errorResult 路由失败的对象
     */
    @MainThread
    void onError(@NonNull EHiRouterErrorResult errorResult);

    /**
     * 两个参数肯定有一个不会为空
     *
     * @param successResult 路由成功时候的返回对象,成功的对象
     * @param errorResult   发生的错误对象
     */
    @MainThread
    void onEvent(@Nullable EHiRouterResult successResult, @Nullable EHiRouterErrorResult errorResult);

    /**
     * 当取消{@link NavigationDisposable#cancel()}的时候调用这个方法
     *
     * @param originalRequest 最原始的请求,当构建 request 的时候发生错误,然后调用 {@link NavigationDisposable#cancel()}
     *                        的时候不会调用这个方法,因为这时候是不可以取消的,因为压根就没开始,当 request 构建出来了
     *                        这时候就可以取消了,所以在真正取消的时候,一定会有这个参数的
     */
    @MainThread
    void onCancel(@NonNull EHiRouterRequest originalRequest);

}
