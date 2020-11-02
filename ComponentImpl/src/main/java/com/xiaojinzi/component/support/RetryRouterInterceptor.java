package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.impl.Navigator;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个拦截器是用来处理拦截器组的.
 * 平常的拦截器某一个处理失败了, 那么就表示整个路由失败了.
 * Retry 拦截器表示重试 N 次失败了, 才算失败, 某一个失败还会走接下来的
 */
public class RetryRouterInterceptor implements RouterInterceptor {

    /**
     * 需要重试的拦截器
     */
    @NonNull
    private RouterInterceptor target;

    private int retryCount;

    public RetryRouterInterceptor(@NonNull RouterInterceptor target, int retryCount) {
        Utils.checkNullPointer(target);
        this.target = target;
        this.retryCount = retryCount;
    }

    @Override
    public void intercept(@NonNull final Chain outChain) throws Exception {

        final RouterInterceptor.Callback outCallback = outChain.callback();

        List<RouterInterceptor> retryList = new ArrayList<>(retryCount + 1);
        for (int i = 0; i < retryCount; i++) {
            retryList.add(target);
        }
        retryList.add(new RouterInterceptor() {
            @Override
            public void intercept(@NonNull Chain chain) throws Exception {
                outChain.proceed(chain.request());
            }
        });

        final RetryInterceptorChain retryChain = new RetryInterceptorChain(
                retryList,
                0, outChain.request(), outCallback
        );
        retryChain.proceed(outChain.request());

    }

    private class RetryInterceptorChain extends Navigator.InterceptorChain {

        private Callback mRetryCallback = new Callback() {

            @Override
            public void onSuccess(@NonNull RouterResult result) {
                rawCallback().onSuccess(result);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                if (index() == interceptors().size() - 2) { // 还没有执行完毕
                    rawCallback().onError(error);
                } else {
                    final RetryInterceptorChain retryChain = new RetryInterceptorChain(
                            interceptors(), index() + 1, request(), rawCallback()
                    );
                    retryChain.proceed(request());
                }
            }

            @Override
            public boolean isComplete() {
                return rawCallback().isComplete();
            }

            @Override
            public boolean isCanceled() {
                return rawCallback().isCanceled();
            }

            @Override
            public boolean isEnd() {
                return rawCallback().isEnd();
            }

        };

        /**
         * @param interceptors 拦截器的即可
         * @param index        要执行的拦截器的下标
         * @param request      第一次这个对象是不需要的
         * @param callback     用户的 {@link Callback}
         */
        public RetryInterceptorChain(@NonNull List<RouterInterceptor> interceptors, int index,
                                     @NonNull RouterRequest request,
                                     @NonNull final Callback callback) {
            super(interceptors, index, request, callback);
        }

        @Override
        public Callback callback() {
            return mRetryCallback;
        }

    }

}
