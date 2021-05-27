package com.xiaojinzi.component.impl.service;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.anno.support.NotAppUseAnno;
import com.xiaojinzi.component.error.NotSupportException;
import com.xiaojinzi.component.error.ServiceRepeatCreateException;
import com.xiaojinzi.component.support.Callable;
import com.xiaojinzi.component.support.DecoratorCallable;
import com.xiaojinzi.component.support.SingletonCallable;
import com.xiaojinzi.component.support.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private static final Map<Class, HashMap<String, DecoratorCallable<?>>> serviceDecoratorMap = new HashMap<>();

    private static final Set<String> uniqueServiceSet = new HashSet<>();

    /**
     * 需要自动初始化的 Service 的 class
     */
    private static final Set<Class> autoInitSet = new HashSet<>();

    /**
     * 注册自动注册的 Service Class
     */
    public static <T> void registerAutoInit(@NonNull Class<T> tClass) {
        Utils.checkNullPointer(tClass, "tClass");
        autoInitSet.add(tClass);
    }

    /**
     * 反注册自动注册的 Service Class
     */
    public static <T> void unregisterAutoInit(@NonNull Class<T> tClass) {
        Utils.checkNullPointer(tClass, "tClass");
        autoInitSet.remove(tClass);
    }

    /**
     * 初始化那些需要自动初始化的 Service
     */
    @WorkerThread
    @NotAppUseAnno
    public static void autoInitService() {
        for (Class targetClass : autoInitSet) {
            // 初始化实现类
            ServiceManager.get(targetClass);
        }
    }

    /**
     * 注册一个装饰者
     *
     * @param tClass   装饰目标的接口
     * @param uid      注册的这个装饰者的唯一的标记
     * @param callable 装饰者的对象获取者
     * @param <T>      装饰目标
     */
    @AnyThread
    @NotAppUseAnno
    public static <T> void registerDecorator(@NonNull Class<T> tClass,
                                             @NonNull String uid,
                                             @NonNull DecoratorCallable<? extends T> callable) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(uid, "uid");
        Utils.checkNullPointer(callable, "callable");
        synchronized (serviceDecoratorMap) {
            HashMap<String, DecoratorCallable<?>> map = serviceDecoratorMap.get(tClass);
            if (map == null) {
                map = new HashMap<>();
                serviceDecoratorMap.put(tClass, map);
            }
            if (serviceDecoratorMap.containsKey(uid)) {
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
        synchronized (serviceDecoratorMap) {
            HashMap<String, DecoratorCallable<?>> map = serviceDecoratorMap.get(tClass);
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
    public static <T> T decorate(@NonNull final Class<T> tClass, @NonNull T target) {
        Utils.checkNullPointer(tClass, "tClass");
        Utils.checkNullPointer(target, "target");
        T result = target;
        synchronized (serviceDecoratorMap) {
            HashMap<String, DecoratorCallable<?>> map = serviceDecoratorMap.get(tClass);
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
     * Service 的创建可能不是在主线程. 所以Service 初始化的时候请注意这一点.
     * 内部会保证创建的过程是线程安全的
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
            String uniqueName = tClass.getName() + ":" + name;
            if (uniqueServiceSet.contains(uniqueName)) {
                throw new ServiceRepeatCreateException("className is " + tClass.getName() + ", serviceName is '" + name + "'");
            }
            uniqueServiceSet.add(uniqueName);
            T result = null;
            HashMap<String, Callable<?>> implServiceMap = serviceMap.get(tClass);
            if (implServiceMap != null) {
                Callable<?> callable = implServiceMap.get(name);
                if (callable != null) {
                    // 如果没创建, 这时候会创建了目标 service 对象
                    T t = (T) Utils.checkNullPointer(callable.get());
                    result = decorate(tClass, t);
                }
            }
            uniqueServiceSet.remove(uniqueName);
            return result;
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
