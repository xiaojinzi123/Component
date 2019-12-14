package com.xiaojinzi.base.interceptor;

import com.xiaojinzi.component.impl.RouterInterceptor;

/**
 * 耗时拦截器
 * time   : 2018/12/04
 *
 * @author : xiaojinzi
 */
public class TimeConsumingInterceptor implements RouterInterceptor {
    @Override
    public void intercept(final Chain chain) throws Exception {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                chain.proceed(chain.request());
            }
        }.start();
    }
}
