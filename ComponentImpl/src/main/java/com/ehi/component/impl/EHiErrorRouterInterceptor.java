package com.ehi.component.impl;

import android.support.annotation.MainThread;

/**
 * 当发生错误的时候的错误拦截器
 */
@MainThread
public interface EHiErrorRouterInterceptor {

    /**
     * 发生错误的时候的回调
     *
     * @param e
     */
    @MainThread
    void onRouterError(Exception e) throws Exception;

}