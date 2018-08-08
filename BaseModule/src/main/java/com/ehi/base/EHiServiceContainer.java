package com.ehi.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * EHi 服务的容器
 */
public class EHiServiceContainer {

    private static Map<Class,Object> map = new HashMap();

    public static<T> void register(@NonNull Class<T> tClass, T t) {
        map.put(tClass, t);
    }

    @Nullable
    public static<T> T unregister(@NonNull Class<T> tClass) {
        return (T)map.remove(tClass);
    }

    @Nullable
    public static <T> T get(@NonNull Class<T> tClass) {
        return (T) map.get(tClass);
    }

}
