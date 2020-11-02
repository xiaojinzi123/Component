package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.anno.RetryAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;

public class RouterRetrySupport {

    private RouterRetrySupport() {
    }

    @NonNull
    public static RouterInterceptor assemblyRetry(@NonNull RouterInterceptor target) {
        RetryAnno retryAnno = target.getClass().getAnnotation(RetryAnno.class);
        if (retryAnno == null) {
            return target;
        }
        int retryCount = retryAnno.value();
        if (retryCount < 1) {
            throw new IllegalArgumentException("retry count must > 0");
        }
        if (retryCount == 1) {
            return target;
        }
        return new RetryRouterInterceptor(target, retryCount);
    }

}
