package com.xiaojinzi.component.impl;

import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.support.Function;
import com.xiaojinzi.component.support.Utils;

/**
 * 当整个流程完成的时候,回调这个接口
 * <p>
 * 详细的请查看 {@link Callback}
 *
 * @author xiaojinzi 30212
 */
public interface BiCallback<T> extends OnRouterCancel, OnRouterError {

    /**
     * 当路由成功的时候,回调
     *
     * @param result 路由成功的对象
     * @param t      返回的对象
     */
    @MainThread
    void onSuccess(@NonNull RouterResult result, @NonNull T t);

    /**
     * 做一个转化
     *
     * @param <T>
     * @param <R>
     */
    abstract class Map<T, R> implements BiCallback<T>,Function<T, R> {

        @NonNull
        private BiCallback targetBiCallback;

        public Map(@NonNull BiCallback targetBiCallback) {
            this.targetBiCallback = targetBiCallback;
        }

        @Override
        public void onSuccess(@NonNull RouterResult result, @NonNull T t) {
            try {
                targetBiCallback.onSuccess(result, Utils.checkNullPointer(apply(t), "apply(t)"));
            } catch (Exception e) {
                targetBiCallback.onError(new RouterErrorResult(e));
            }
        }

        @Override
        public void onCancel(@Nullable RouterRequest originalRequest) {
            targetBiCallback.onCancel(originalRequest);
        }

        @Override
        public void onError(@NonNull RouterErrorResult errorResult) {
            targetBiCallback.onError(errorResult);
        }

    }

    /**
     * 空白实现类
     */
    class BiCallbackAdapter<T> implements BiCallback<T> {

        @CallSuper
        @Override
        public void onSuccess(@NonNull RouterResult result, @NonNull T t) {
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
