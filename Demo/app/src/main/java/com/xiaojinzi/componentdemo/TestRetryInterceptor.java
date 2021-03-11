package com.xiaojinzi.componentdemo;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.anno.GlobalInterceptorAnno;
import com.xiaojinzi.component.anno.RetryAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;

@RetryAnno(2)
// @GlobalInterceptorAnno
public class TestRetryInterceptor implements RouterInterceptor {

    @Override
    public void intercept(@NonNull Chain chain) throws Exception {

        boolean isNext = false;

        if (isNext) {
            chain.proceed(chain.request());
        } else {
            chain.callback().onError(new Exception("TestRetryInterceptor"));
        }

    }

}
