package com.ehi.component.support;

import android.support.annotation.WorkerThread;

import com.ehi.component.impl.EHiRouterExecuteResult;
import com.ehi.component.impl.EHiRouterRequest;

/**
 * 路由跳转的拦截器
 */
public interface EHiRouterInterceptor {

    /**
     * 拦截器
     *
     * @param chain
     */
    @WorkerThread
    EHiRouterExecuteResult intercept(Chain chain) throws Exception;

    /**
     * 执行器
     */
    interface Chain {

        /**
         * 拿到请求对象
         *
         * @return
         */
        EHiRouterRequest request();

        /**
         * 执行这个跳转
         *
         * @return
         */
        @WorkerThread
        EHiRouterExecuteResult proceed(EHiRouterRequest request) throws Exception;

    }

}