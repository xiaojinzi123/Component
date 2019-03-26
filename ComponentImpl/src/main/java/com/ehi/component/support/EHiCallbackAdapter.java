package com.ehi.component.support;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.impl.Callback;
import com.ehi.component.impl.RouterErrorResult;
import com.ehi.component.impl.RouterRequest;
import com.ehi.component.impl.RouterResult;

/**
 * 接口的空实现
 */
public class EHiCallbackAdapter implements Callback {

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
