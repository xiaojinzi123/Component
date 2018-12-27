package com.ehi.component.support;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.ehi.component.impl.EHiRouterExecuteResult;
import com.ehi.component.impl.EHiRouterRequest;

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
    void intercept(Chain chain) throws Exception;

    /**
     * 执行器
     */
    @MainThread
    interface Chain {

        /**
         * 拿到请求对象
         *
         * @return
         */
        @NonNull
        EHiRouterRequest request();

        /**
         * 获取回调对象
         *
         * @return
         */
        @NonNull
        Callback callback();

        /**
         * 执行这个跳转
         *
         * @return
         */
        @MainThread
        void proceed(EHiRouterRequest request) throws Exception;

    }

    /**
     * 回调对象,错误和成功的方法均智能调用一次,多次调用只有第一次有用,其他会被忽略
     */
    @MainThread
    interface Callback {

        /**
         * 成功的回调
         *
         * @param result
         */
        @MainThread
        void onSuccess(EHiRouterExecuteResult result);

        /**
         * 错误的回调
         *
         * @param error
         */
        @MainThread
        void onError(Exception error);

        /**
         * 是否完成了
         *
         * @return
         */
        @MainThread
        boolean isComplete();

        /**
         * 是否已经取消了
         *
         * @return
         */
        boolean isCanceled();

        /**
         * 取消
         */
        void cancel();

    }

}