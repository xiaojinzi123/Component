package com.ehi.component.support;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.impl.EHiCallback;
import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.impl.EHiRouterResult;

/**
 * 接口的空实现
 */
public class EHiCallbackAdapter implements EHiCallback {

    @Override
    @MainThread
    public void onSuccess(@NonNull EHiRouterResult result) {
    }

    @Override
    public void onError(@Nullable EHiRouterRequest originalRequest, @NonNull Throwable error) {
    }

    @Override
    public void onEvent(@Nullable EHiRouterRequest originalRequest, @Nullable EHiRouterResult routerResult, @Nullable Throwable error) {
    }

    @Override
    @MainThread
    public void onCancel(@NonNull EHiRouterRequest request) {
    }

}
