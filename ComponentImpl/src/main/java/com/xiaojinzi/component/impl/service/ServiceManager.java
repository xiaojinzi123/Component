package com.xiaojinzi.component.impl.service;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.error.NotSupportException;
import com.xiaojinzi.component.support.Action;
import com.xiaojinzi.component.support.CallNullable;
import com.xiaojinzi.component.support.Callable;
import com.xiaojinzi.component.support.SingletonCallable;
import com.xiaojinzi.component.support.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 服务的容器,使用这个服务容器你需要判断获取到的服务是否为空,对于使用者来说还是比较不方便的
 * 建议使用 Service 扩展的版本 RxService
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
public class ServiceManager {

    private ServiceManager() {
        throw new NotSupportException("can't to create");
    }

    /**
     * Service 的集合. 线程安全的
     */
    private static Map<Class, Callable<?>> serviceMap =
            Collections.synchronizedMap(new HashMap<Class, Callable<?>>());

    /**
     * 你可以注册一个服务,服务的初始化可以是懒加载的
     * 注册的时候, 不会初始化目标 Service 的
     * {@link #get(Class)} 方法内部才会初始化目标 Service
     */
    @AnyThread
    public static <T> void register(@NonNull Class<T> tClass, @NonNull Callable<? extends T> callable) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(callable, "callable");
        unregister(tClass);
        serviceMap.put(tClass, callable);
    }

    @Nullable
    @AnyThread
    public static <T> void unregister(@NonNull Class<T> tClass) {
        Utils.checkNullPointer(tClass, "tClass");
        // 需要判断到是否已经初始化了, 如果还没初始化就返回 null
        Callable<?> callable = serviceMap.remove(tClass);
        if (callable == null) {
            return;
        }
        if (callable instanceof SingletonCallable) {
            if (((SingletonCallable<Object>) callable).isInit()) {
                ((SingletonCallable<Object>) callable).destroy();
            }
        }
    }

    /**
     * 如果不是主线程会卡住线程, 让最终的用户自定义的对象在主线程中被创建
     *
     * @param tClass 目标对象的 Class 对象
     * @param <T>    目标对象的实例对象
     * @return 目标对象的实例对象
     */
    @Nullable
    @AnyThread
    public static <T> T get(@NonNull final Class<T> tClass) {
        return Utils.mainThreadCallNullable(new CallNullable<T>() {
            @NonNull
            @Override
            public T get() {
                Callable<?> callable = serviceMap.get(tClass);
                if (callable == null) {
                    return null;
                } else {
                    // 如果没创建, 这时候会创建了目标 service 对象
                    T t = (T) Utils.checkNullPointer(callable.get());
                    return t;
                }
            }
        });
    }

    /**
     * @return 肯定不会为 null
     * @see #get(Class)
     */
    @NonNull
    @AnyThread
    public static <T> T requiredGet(@NonNull final Class<T> tClass) {
        return Utils.checkNullPointer(get(tClass));
    }

}
