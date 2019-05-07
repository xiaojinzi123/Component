package com.xiaojinzi.component.impl;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.support.NavigationDisposable;

/**
 * 当路由完成的时候,回调这个接口,这时候的完成不一定是成功的,可能是失败的,成功和失败都表示完成
 * 所有的调用顺序整理：
 * 1.{@link #onSuccess(RouterResult)} --> {@link #onEvent(RouterResult, RouterErrorResult)}
 * 2.{@link #onError(RouterErrorResult)} --> {@link #onEvent(RouterResult, RouterErrorResult)}
 * 3.被取消的时候：{@link #onCancel(RouterRequest)}
 *
 * @author xiaojinzi 30212
 */
public interface Callback extends OnCancel {

    /**
     * 当路由成功的时候,回调
     *
     * @param result 路由成功的对象
     */
    @MainThread
    void onSuccess(@NonNull RouterResult result);

    /**
     * 当路由错误的时候回调
     *
     * @param errorResult 路由失败的对象
     */
    @MainThread
    void onError(@NonNull RouterErrorResult errorResult);

    /**
     * 两个参数肯定有一个不会为空
     *
     * @param successResult 路由成功时候的返回对象,成功的对象
     * @param errorResult   发生的错误对象
     */
    @MainThread
    void onEvent(@Nullable RouterResult successResult, @Nullable RouterErrorResult errorResult);

}
