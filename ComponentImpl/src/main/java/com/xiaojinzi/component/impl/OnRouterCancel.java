package com.xiaojinzi.component.impl;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.support.NavigationDisposable;

/**
 * 被取消了回调接口
 */
public interface OnRouterCancel {

    /**
     * 当取消{@link NavigationDisposable#cancel()}的时候调用这个方法
     * 取消的时候可能 {@link RouterRequest} 都没有创建,所以参数有可能为空
     *
     * @param originalRequest 最原始的请求,当构建 {@link RouterRequest} 的时候发生错误,然后调用 {@link NavigationDisposable#cancel()}
     *                        的时候不会调用这个方法,因为这时候是不可以取消的,因为压根就没开始,当 request 构建出来了
     *                        这时候就可以取消了,所以在真正取消的时候,一定会有这个参数的
     */
    @MainThread
    void onCancel(@Nullable RouterRequest originalRequest);

}
