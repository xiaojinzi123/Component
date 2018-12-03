package com.ehi.base.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * EHi 服务的容器
 */
public class EHiServiceContainer {

    private static Map<Class, IServiceLoad<?>> map = new HashMap();

    /**
     * 默认注册的服务都是单例的,并且是注册的时候就实例化了,因为对象都是传进来的
     *
     * @param tClass
     * @param t
     * @param <T>
     */
    public static <T> void register(@NonNull Class<T> tClass, @NonNull final T t) {
        SingletonService<T> iServiceLoad = new SingletonService<T>() {
            @Override
            public T getRaw() {
                return t;
            }
        };
        register(tClass, iServiceLoad);
    }

    /**
     * 你可以注册一个服务,服务的初始化可以是 懒加载的
     *
     * @param tClass
     * @param iServiceLoad
     * @param <T>
     */
    public static <T> void register(@NonNull Class<T> tClass, @NonNull IServiceLoad<T> iServiceLoad) {
        map.put(tClass, iServiceLoad);
    }

    @Nullable
    public static <T> T unregister(@NonNull Class<T> tClass) {
        return (T) map.remove(tClass);
    }

    @Nullable
    public static <T> T get(@NonNull Class<T> tClass) {
        if (map.get(tClass) == null) {
            return null;
        }else {
            return (T) map.get(tClass).get();
        }
    }

}
