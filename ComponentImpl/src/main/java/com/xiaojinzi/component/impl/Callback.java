package com.xiaojinzi.component.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.support.OnRouterCancel;
import com.xiaojinzi.component.support.OnRouterError;
import com.xiaojinzi.component.support.OnRouterSuccess;

/**
 * 当路由完成的时候,回调这个接口,这时候的完成不一定是成功的,可能是失败的,成功和失败都表示完成
 * 所有的调用顺序整理：
 * 1.{@link #onSuccess(RouterResult)} --> {@link #onEvent(RouterResult, RouterErrorResult)}
 * 2.{@link #onError(RouterErrorResult)} --> {@link #onEvent(RouterResult, RouterErrorResult)}
 * 3.被取消的时候：{@link #onCancel(RouterRequest)}
 *
 * @author xiaojinzi
 */
@UiThread
@CheckClassNameAnno
public interface Callback extends OnRouterSuccess, OnRouterError, OnRouterCancel {

    /**
     * 两个参数肯定有一个不会为空
     *
     * @param successResult 路由成功时候的返回对象,成功的对象
     * @param errorResult   发生的错误对象
     */
    @UiThread
    void onEvent(@Nullable RouterResult successResult, @Nullable RouterErrorResult errorResult);

}
