package com.ehi.component.impl.interceptor;

import android.net.Uri;

import com.ehi.component.error.NavigationFailException;
import com.ehi.component.impl.EHiRouterInterceptor;

/**
 * 这个拦截器必须在其他任何一个拦截器之前执行
 * 从根本上限制同一个界面在一秒钟内只能打开一次,这个拦截器会被框架最先执行
 * time   : 2019/01/23
 *
 * @author : xiaojinzi 30212
 */
public class EHiOpenOnceInterceptor implements EHiRouterInterceptor {

    private EHiOpenOnceInterceptor() {
    }

    private static class SingletonInstance {
        private static final EHiOpenOnceInterceptor INSTANCE = new EHiOpenOnceInterceptor();
    }

    public static EHiOpenOnceInterceptor getInstance() {
        return EHiOpenOnceInterceptor.SingletonInstance.INSTANCE;
    }

    private String preHost;
    private String prePath;
    /**
     * 记录上一个界面跳转的时间
     */
    private long preTargetTime;

    @Override
    public void intercept(Chain chain) throws Exception {
        Uri uri = chain.request().uri;
        String currentHost = uri.getHost();
        String currentPath = uri.getPath();
        long currentTime = System.currentTimeMillis();
        // 如果匹配了
        if (currentHost.equals(preHost) && currentPath.equals(prePath) && (currentTime - preTargetTime) < 1000) {
            chain.callback().onError(new NavigationFailException("target '" + uri.toString() + "' can't launch twice in a second"));
        } else {
            preHost = currentHost;
            prePath = currentPath;
            preTargetTime = currentTime;
            // 放过执行
            chain.proceed(chain.request());
        }
    }

}
