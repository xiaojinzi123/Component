package com.xiaojinzi.component.support;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.impl.Callback;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;

/**
 * 接口的空实现
 */
public class CallbackAdapter implements Callback {

    @Override
    @MainThread
    public void onSuccess(@NonNull RouterResult result) {
        // empty
    }

    @Override
    @MainThread
    public void onError(RouterErrorResult errorResult) {
        // empty
    }

    @Override
    @MainThread
    public void onEvent(@Nullable RouterResult successResult, @Nullable RouterErrorResult errorResult) {
        // empty
    }

    @Override
    @MainThread
    public void onCancel(@NonNull RouterRequest originalRequest) {
        // empty
    }

}
