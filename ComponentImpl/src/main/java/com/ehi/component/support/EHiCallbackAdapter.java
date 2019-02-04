package com.ehi.component.support;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.impl.EHiCallback;
import com.ehi.component.impl.EHiRouterErrorResult;
import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.impl.EHiRouterResult;

/**
 * 接口的空实现
 */
public class EHiCallbackAdapter implements EHiCallback {

    @Override
    public void onSuccess(@NonNull EHiRouterResult result) {
    }

    @Override
    public void onError(EHiRouterErrorResult errorResult) {
    }

    @Override
    public void onEvent(@Nullable EHiRouterResult successResult, @Nullable EHiRouterErrorResult errorResult) {
    }

    @Override
    public void onCancel(@NonNull EHiRouterRequest request) {
    }

}
