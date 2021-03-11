package com.xiaojinzi.component.impl.service;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.anno.support.NotAppUseAnno;
import com.xiaojinzi.component.error.NotSupportException;
import com.xiaojinzi.component.support.Callable;
import com.xiaojinzi.component.support.DecoratorCallable;
import com.xiaojinzi.component.support.SingletonCallable;
import com.xiaojinzi.component.support.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 服务的容器,使用这个服务容器你需要判断获取到的服务是否为空,对于使用者来说还是比较不方便的
 * 建议使用 Service 扩展的版本 RxService
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
public class ServiceManager {

    public static final String DEFAULT_NAME = "";

    private ServiceManager() {
        throw new NotSupportException("can't to create");
    }

    /**
     * Service 的集合. 线程不安全的
     */
    private static final Map<Class, HashMap<String, Callable<?>>> serviceMap = new HashMap<>();

    /**
     * Service 装饰者的集合. 线程不安全的
     */
    private static final Map<Class, HashMap<String, DecoratorCallable<?>>> serviceDecorateMap = new HashMap<>();

    /**
     * 注册一个装饰者
     *
     * @param tClass   装饰目标的接口
     * @param uid      注册的这个装饰者的唯一的标记
     * @param priority 优先级, 值越大, 对应的装饰者执行的顺序越靠前
     * @param callable 装饰者的对象获取者
     * @param <T>      装饰目标
     */
    @AnyThread
    @NotAppUseAnno
    public static <T> void registerDecorator(@NonNull Class<T> tClass,
                                             @NonNull String uid,
                                             int priority,
                                             @NonNull DecoratorCallable<? extends T> callable) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(uid, "uid");
        Utils.checkNullPointer(callable, "callable");
        synchronized (serviceDecorateMap) {
            HashMap<String, DecoratorCallable<?>> map = serviceDecorateMap.get(tClass);
            if (map == null) {
                map = new HashMap<>();
                serviceDecorateMap.put(tClass, map);
            }
            if (serviceDecorateMap.containsKey(uid)) {
                throw new RuntimeException(tClass.getSimpleName() + " the key of '" + uid + "' is exist");
            }
            map.put(uid, callable);
        }
    }

    /**
     * 注册一个装饰者
     *
     * @param tClass 装饰目标的接口
     * @param uid    注册的这个装饰者的唯一的标记
     * @param <T>    装饰目标
     */
    @AnyThread
    @NotAppUseAnno
    public static <T> void unregisterDecorator(@NonNull Class<T> tClass, @NonNull String uid) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(uid, "uid");
        synchronized (serviceDecorateMap) {
            HashMap<String, DecoratorCallable<?>> map = serviceDecorateMap.get(tClass);
            if (map != null) {
                map.remove(uid);
            }
        }
    }

    @AnyThread
    @NotAppUseAnno
    public static <T> void register(@NonNull Class<T> tClass, @NonNull Callable<? extends T> callable) {
        register(tClass, DEFAULT_NAME, callable);
    }

    /**
     * 你可以注册一个服务,服务的初始化可以是懒加载的
     * 注册的时候, 不会初始化目标 Service 的
     * {@link #get(Class)} 方法内部才会初始化目标 Service
     */
    @AnyThread
    @NotAppUseAnno
    public static <T> void register(@NonNull Class<T> tClass, @NonNull String name, @NonNull Callable<? extends T> callable) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(name, "name");
        Utils.checkNullPointer(callable, "callable");
        synchronized (serviceMap) {
            HashMap<String, Callable<?>> implServiceMap = serviceMap.get(tClass);
            if (implServiceMap == null) {
                implServiceMap = new HashMap<>();
                serviceMap.put(tClass, implServiceMap);
            }
            if (implServiceMap.containsKey(name)) {
                throw new RuntimeException(tClass.getSimpleName() + " the key of '" + name + "' is exist");
            }
            implServiceMap.put(name, callable);
        }
    }

    @Nullable
    @AnyThread
    @NotAppUseAnno
    public static <T> void unregister(@NonNull Class<T> tClass) {
        unregister(tClass, DEFAULT_NAME);
    }

    @Nullable
    @AnyThread
    @NotAppUseAnno
    public static <T> void unregister(@NonNull Class<T> tClass, @NonNull String name) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(name, "name");
        synchronized (serviceMap) {
            HashMap<String, Callable<?>> implServiceMap = serviceMap.get(tClass);
            if (implServiceMap != null) {
                Callable<?> callable = implServiceMap.remove(name);
                if (callable == null) {
                    return;
                }
                if (callable instanceof SingletonCallable) {
                    if (((SingletonCallable<Object>) callable).isInit()) {
                        ((SingletonCallable<Object>) callable).destroy();
                    }
                }
            }
        }
    }

    /**
     * 装饰某一个 Service
     *
     * @param tClass   目标 Service class
     * @param target   目标对象
     * @param <target> 目标对象
     * @return 返回一个增强的目标对象的装饰者
     */
    @NotAppUseAnno
    private static <T> T decorate(@NonNull final Class<T> tClass, @NonNull T target) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(target, "target");
        T result = target;
        synchronized (serviceDecorateMap) {
            HashMap<String, DecoratorCallable<?>> map = serviceDecorateMap.get(tClass);
            if (map != null) {
                Collection<DecoratorCallable<?>> values = map.values();
                if (values != null) {
                    // 排序
                    List<DecoratorCallable<?>> list = new ArrayList<>(values);
                    Collections.sort(list, new Comparator<DecoratorCallable<?>>() {
                        @Override
                        public int compare(DecoratorCallable<?> o1, DecoratorCallable<?> o2) {
                            return o1.priority() - o2.priority();
                        }
                    });
                    for (DecoratorCallable<?> callable : list) {
                        DecoratorCallable<T> realCallable = (DecoratorCallable<T>) callable;
                        result = realCallable.get(result);
                    }
                }
            }
        }
        return result;
    }

    @Nullable
    @AnyThread
    public static <T> T get(@NonNull final Class<T> tClass) {
        return get(tClass, DEFAULT_NAME);
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
    public static <T> T get(@NonNull final Class<T> tClass, @NonNull String name) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(name, "name");
        synchronized (serviceMap) {
            HashMap<String, Callable<?>> implServiceMap = serviceMap.get(tClass);
            if (implServiceMap == null) {
                return null;
            }
            Callable<?> callable = implServiceMap.get(name);
            if (callable == null) {
                return null;
            } else {
                // 如果没创建, 这时候会创建了目标 service 对象
                T t = (T) Utils.checkNullPointer(callable.get());
                return decorate(tClass, t);
            }
        }
    }

    /**
     * @return 肯定不会为 null
     * @see #get(Class)
     */
    @NonNull
    @AnyThread
    public static <T> T requiredGet(@NonNull final Class<T> tClass) {
        return requiredGet(tClass, DEFAULT_NAME);
    }

    /**
     * @return 肯定不会为 null
     * @see #get(Class)
     */
    @NonNull
    @AnyThread
    public static <T> T requiredGet(@NonNull final Class<T> tClass, @NonNull String name) {
        return Utils.checkNullPointer(get(tClass, name));
    }

}
