package com.xiaojinzi.component.impl;

import android.support.annotation.NonNull;

/**
 * 这个类表示一次成功路由的返回结果对象
 * time   : 2018/11/10
 *
 * @author : xiaojinzi
 */
public class RouterResult {

    /**
     * 最原始的请求,谁都更改不了的,而且不可能为空在这里
     */
    @NonNull
    private final RouterRequest mOriginalRequest;

    /**
     * 如果成功了,这个会有值,这个可能不是最原始的请求啦,可能是拦截器修改过或者
     * 整个 request 对象都被改了
     */
    @NonNull
    private final RouterRequest mFinalRequest;

    /**
     * @param originalRequest 最原始的请求
     * @param finalRequest    可能修改过的请求,也可能是和原始请求一样
     */
    public RouterResult(@NonNull RouterRequest originalRequest, @NonNull RouterRequest finalRequest) {
        this.mOriginalRequest = originalRequest;
        this.mFinalRequest = finalRequest;
    }

    /**
     * 最原始的请求
     *
     * @return 最原始的请求
     */
    @NonNull
    public RouterRequest getOriginalRequest() {
        return mOriginalRequest;
    }

    /**
     * 获取可能由拦截器修改过的 request 对象,大部分没有被修改的其实就是最原始的 request 对象
     *
     * @return
     */
    @NonNull
    public RouterRequest getFinalRequest() {
        return mFinalRequest;
    }

}
