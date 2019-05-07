package com.xiaojinzi.component.impl;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.support.NavigationDisposable;

/**
 * 当整个流程完成的时候,回调这个接口
 * <p>
 * 详细的请查看 {@link Callback}
 *
 * @author xiaojinzi 30212
 */
public interface BiCallback<T> {

    /**
     * 当路由成功的时候,回调
     *
     * @param result 路由成功的对象
     * @param t      返回的对象
     */
    @MainThread
    void onSuccess(@NonNull RouterResult result, @NonNull T t);

    /**
     * 当路由错误的时候回调
     *
     * @param errorResult 路由失败的对象
     */
    @MainThread
    void onError(@NonNull RouterErrorResult errorResult);

    /**
     * 当取消{@link NavigationDisposable#cancel()}的时候调用这个方法
     *
     * @param originalRequest 最原始的请求,当构建 request 的时候发生错误,然后调用 {@link NavigationDisposable#cancel()}
     *                        的时候不会调用这个方法,因为这时候是不可以取消的,因为压根就没开始,当 request 构建出来了
     *                        这时候就可以取消了,所以在真正取消的时候,一定会有这个参数的
     */
    @MainThread
    void onCancel(@NonNull RouterRequest originalRequest);

}
