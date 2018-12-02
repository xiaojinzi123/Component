package com.ehi.component.support;

import android.support.annotation.MainThread;

import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.impl.EHiRouterResult;

/**
 * 路由跳转的拦截器
 */
@MainThread
public interface EHiRouterInterceptor {

    /**
     * 拦截器
     *
     * @param chain
     */
    @MainThread
    EHiRouterResult intercept(Chain chain) throws Exception;

    interface Chain {

        /**
         * 拿到请求对象
         *
         * @return
         */
        @MainThread
        EHiRouterRequest request();

        /**
         * 执行这个跳转
         *
         * @return
         */
        @MainThread
        EHiRouterResult proceed(EHiRouterRequest request) throws Exception;

    }

}