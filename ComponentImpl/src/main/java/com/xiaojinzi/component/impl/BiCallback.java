package com.xiaojinzi.component.impl;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.support.Function;
import com.xiaojinzi.component.support.OnRouterCancel;
import com.xiaojinzi.component.support.OnRouterError;
import com.xiaojinzi.component.support.Utils;

/**
 * 当整个流程完成的时候,回调这个接口
 * <p>
 * 详细的请查看 {@link Callback}
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
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
     * @param <T> T 转化为 R
     * @param <R> T 转化为 R
     */
    abstract class Map<T, R> implements BiCallback<T>, Function<T, R> {

        @NonNull
        private BiCallback targetBiCallback;

        public Map(@NonNull BiCallback targetBiCallback) {
            Utils.checkNullPointer(targetBiCallback, "targetBiCallback");
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

        @Override
        public void onSuccess(@NonNull RouterResult result, @NonNull T t) {
        }

        @Override
        public void onError(@NonNull RouterErrorResult errorResult) {
        }

        @Override
        public void onCancel(@NonNull RouterRequest originalRequest) {
        }

    }

}
