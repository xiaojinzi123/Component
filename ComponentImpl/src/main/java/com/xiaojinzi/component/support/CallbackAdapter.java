package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.xiaojinzi.component.impl.Callback;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;

/**
 * 接口的空实现
 */
public class CallbackAdapter implements Callback {

    @Override
    @UiThread
    public void onSuccess(@NonNull RouterResult result) {
        // empty
    }

    @Override
    @UiThread
    public void onError(RouterErrorResult errorResult) {
        // empty
    }

    @Override
    @UiThread
    public void onEvent(@Nullable RouterResult successResult, @Nullable RouterErrorResult errorResult) {
        // empty
    }

    @Override
    @UiThread
    public void onCancel(@NonNull RouterRequest originalRequest) {
        // empty
    }

}
