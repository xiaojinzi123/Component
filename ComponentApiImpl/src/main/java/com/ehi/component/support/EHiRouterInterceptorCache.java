package com.ehi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存所有的自定义拦截器
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouterInterceptorCache {

    private static final Map map = new HashMap<>();

    public static synchronized <T> void put(@NonNull Class<T> tClass, @NonNull T t) {
        map.put(tClass, t);
    }

    @Nullable
    public static synchronized <T> T get(@NonNull Class<T> tClass) {
        return (T) map.get(tClass);
    }

}
