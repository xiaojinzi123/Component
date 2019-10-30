package com.xiaojinzi.component.impl.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassName;
import com.xiaojinzi.component.support.Callable;
import com.xiaojinzi.component.support.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务的容器,使用这个服务容器你需要判断获取到的服务是否为空,对于使用者来说还是比较不方便的
 * 建议使用 Service 扩展的版本 RxService
 *
 * @author xiaojinzi 30212
 */
@CheckClassName
public class ServiceManager {

    private ServiceManager() {
    }

    /**
     * Service 的集合
     */
    private static Map<Class, Callable<?>> map = new HashMap<>();

    /**
     * 你可以注册一个服务,服务的初始化可以是 懒加载的
     *
     * @param tClass
     * @param callable
     * @param <T>
     */
    public static <T> void register(@NonNull Class<T> tClass, @NonNull Callable<? extends T> callable) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(callable, "callable");
        map.put(tClass, callable);
    }

    @Nullable
    public static <T> void unregister(@NonNull Class<T> tClass) {
        map.remove(tClass);
    }

    @Nullable
    public static <T> T get(@NonNull Class<T> tClass) {
        Callable<?> callable = map.get(tClass);
        if (callable == null) {
            return null;
        } else {
            return (T) callable.get();
        }
    }

}
