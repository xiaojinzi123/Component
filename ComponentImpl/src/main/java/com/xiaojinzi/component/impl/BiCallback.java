package com.xiaojinzi.component.impl;

import android.support.annotation.CallSuper;
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
public interface BiCallback<T> extends OnCancel {

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
     * 空白实现类
     */
    class BiCallbackAdapter implements BiCallback {

        @CallSuper
        @Override
        public void onSuccess(@NonNull RouterResult result, @NonNull Object o) {
        }

        @CallSuper
        @Override
        public void onError(@NonNull RouterErrorResult errorResult) {
        }

        @CallSuper
        @Override
        public void onCancel(@NonNull RouterRequest originalRequest) {
        }

    }

}
