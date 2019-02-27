package com.ehi.component.impl;

import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

/**
 * 路由跳转的拦截器,设计是 callback 机制代替即时返回的,即时返回比如 OkHttp的拦截器
 * 和这类功能性库不同的是这个是组件路由的拦截器,大部分代码需要在主线程执行,所以设计的初衷：
 * {@link EHiRouterInterceptor#intercept(Chain)} 方法内的代码会在主线程执行,需要用户调用的下面几个方法 {@link Chain#proceed(EHiRouterRequest)}
 * {@link Callback#onError(Throwable)} 和 {@link Callback#onSuccess(EHiRouterResult)} 可以在任何线程调用
 * 这么做的目的也是为了方便用户在任何时候都可以调用而不用关心线程
 */
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
        @AnyThread
        void proceed(@NonNull EHiRouterRequest request);

    }

    /**
     * 回调对象,错误和成功的方法均只能调用一次,多次调用只有第一次有用,其他会被忽略
     */
    @MainThread
    interface Callback {

        /**
         * 成功的回调
         * {@link #onSuccess(EHiRouterResult)} 和 {@link #onError(Throwable)} 都有且只能调用一次,多次调用
         * 的时候第一次调用为准,其他的忽略
         *
         * @param result
         */
        @AnyThread
        void onSuccess(EHiRouterResult result);

        /**
         * 错误的回调
         * {@link #onSuccess(EHiRouterResult)} 和 {@link #onError(Throwable)} 都有且只能调用一次,多次调用
         * 的时候第一次调用为准,其他的忽略
         *
         * @param error
         */
        @AnyThread
        void onError(Throwable error);

        /**
         * 是否完成了
         *
         * @return
         */
        @AnyThread
        boolean isComplete();

        /**
         * 是否已经取消了
         *
         * @return
         */
        @AnyThread
        boolean isCanceled();

    }

}