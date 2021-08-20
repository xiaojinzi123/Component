package com.xiaojinzi.component.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.support.Utils;

/**
 * 为了实现 {@link BiCallback} 用户的这个 Callback 的各个方法最多只能执行一次
 */
class BiCallbackWrap<T> implements BiCallback<T> {

    /**
     * 标记是否结束
     */
    private boolean isEnd;

    @NonNull
    private BiCallback<T> targetBiCallback;

    public BiCallbackWrap(@NonNull BiCallback<T> targetBiCallback) {
        Utils.checkNullPointer(targetBiCallback, "targetBiCallback");
        this.targetBiCallback = targetBiCallback;
    }

    @Override
    public synchronized void onSuccess(@NonNull RouterResult result, @NonNull T t) {
        if (!isEnd) {
            targetBiCallback.onSuccess(result, t);
        }
        isEnd = true;
    }

    @Override
    public synchronized void onCancel(@Nullable RouterRequest originalRequest) {
        if (!isEnd) {
            targetBiCallback.onCancel(originalRequest);
        }
        isEnd = true;
    }

    @Override
    public synchronized void onError(@NonNull RouterErrorResult errorResult) {
        if (!isEnd) {
            targetBiCallback.onError(errorResult);
        }
        isEnd = true;
    }

}
