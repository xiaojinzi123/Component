package com.xiaojinzi.component.impl.interceptor;

import android.net.Uri;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.impl.RouterInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 这个拦截器必须在其他任何一个拦截器之前执行
 * 从根本上限制同一个界面在一秒钟内只能打开一次,这个拦截器会被框架最先执行
 * note: 这个拦截器没有连同 {@link Uri#getScheme()} 一起判断,其实应该一起的,
 * 但是现实中应该也不会出现一秒钟 host 和 path 都相同的两次路由了
 * <p>
 * time   : 2019/01/23
 *
 * @author : xiaojinzi
 */
public class OpenOnceInterceptor implements RouterInterceptor {

    private OpenOnceInterceptor() {
    }

    private static class SingletonInstance {
        private static final OpenOnceInterceptor INSTANCE = new OpenOnceInterceptor();
    }

    public static OpenOnceInterceptor getInstance() {
        return OpenOnceInterceptor.SingletonInstance.INSTANCE;
    }

    private Map<String, Long> map = new HashMap<>();

    @Override
    public void intercept(Chain chain) throws Exception {
        Uri uri = chain.request().uri;
        String hostAndPath = new StringBuffer()
                .append(uri.getHost())
                .append("/").append(uri.getPath())
                .toString();
        // 调试的情况下可能会失效,因为你断点打到这里慢慢的往下走那么可能时间已经过了一秒,就失去了限制的作用
        long currentTime = System.currentTimeMillis();
        // 如果之前有了并且时间少于一定的时间
        if (map.containsKey(hostAndPath) && (currentTime - map.get(hostAndPath)) < Component.getConfig().getRouteRepeatCheckDuration()) {
            chain.callback().onError(new NavigationFailException("same request can't launch twice in a second, target uri is：" + uri.toString()));
        } else {
            map.put(hostAndPath, currentTime);
            // 放过执行
            chain.proceed(chain.request());
        }
        cleanOverdue();
    }

    private void cleanOverdue() {
        long currentTime = System.currentTimeMillis();
        List<String> keys = new ArrayList<>();
        for (String key : map.keySet()) {
            if (currentTime - map.get(key) >= Component.getConfig().getRouteRepeatCheckDuration()) {
                keys.add(key);
            }
        }
        for (String key : keys) {
            map.remove(key);
        }
    }

}
