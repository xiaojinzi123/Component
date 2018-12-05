package com.ehi.component.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.anno.EHiRouterAnno;

import java.util.HashMap;
import java.util.Map;

/**
 * 支持缓存自定义拦截器,工具类
 * 目前就只有给 目标页面在 {@link EHiRouterAnno#interceptors()} 写的拦截器做缓存
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
class EHiRouterInterceptorUtil {

    private static final Map map = new HashMap<>();

    public static synchronized <T> void put(@NonNull Class<T> tClass, @NonNull T t) {
        map.put(tClass, t);
    }

    @Nullable
    public static synchronized <T> T get(@NonNull Class<T> tClass) {
        return (T) map.get(tClass);
    }

}
