package com.ehi.component.impl;

import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

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
        void proceed(@NonNull EHiRouterRequest request);

    }

    /**
     * 回调对象,错误和成功的方法均只能调用一次,多次调用只有第一次有用,其他会被忽略
     */
    @MainThread
    interface Callback {

        /**
         * 成功的回调
         *
         * @param result
         */
        @MainThread
        void onSuccess(EHiRouterResult result);

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

    }

    class CallbackAdapter implements Callback {

        @NonNull
        protected Callback mCallback;

        public CallbackAdapter(@NonNull Callback callback) {
            this.mCallback = callback;
        }

        @Override
        @CallSuper
        public void onSuccess(EHiRouterResult result) {
            mCallback.onSuccess(result);
        }

        @Override
        @CallSuper
        public void onError(Exception error) {
            mCallback.onError(error);
        }

        @Override
        public boolean isComplete() {
            return mCallback.isComplete();
        }

        @Override
        public boolean isCanceled() {
            return mCallback.isCanceled();
        }

    }

}