package com.ehi.component.support;

/**
 * 当发生错误的时候的错误拦截器
 */
public interface EHiErrorRouterInterceptor {

    /**
     * 发生错误的时候的回调
     *
     * @param e
     */
    void onRouterError(Exception e) throws Exception;

}