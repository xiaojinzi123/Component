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
    @MainThread
    public void onError(@NonNull Throwable error) {
    }

    @Override
    @MainThread
    public void onEvent(@NonNull EHiRouterResult result, @Nullable Throwable error) {
    }

    @Override
    @MainThread
    public void onCancel(@NonNull EHiRouterRequest request) {
    }

}
