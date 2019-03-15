package com.ehi.component.impl.interceptor;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.cache.Cache;
import com.ehi.component.cache.CacheType;
import com.ehi.component.cache.DefaultCacheFactory;
import com.ehi.component.error.CreateInterceptorException;
import com.ehi.component.impl.EHiRouterInterceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 支持缓存自定义拦截器,工具类
 * 目前就只有给 目标页面在 {@link EHiRouterAnno#interceptors()}
 * or {@link EHiRouterAnno#interceptorNames()}
 * or {@link com.ehi.component.impl.EHiRouter.Builder#interceptors(Class[])}
 * or {@link com.ehi.component.impl.EHiRouter.Builder#interceptorNames(String...)}
 * 写的拦截器做缓存
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouterInterceptorCache {

    private EHiRouterInterceptorCache() {
    }

    /**
     * 拦截器 Class --> EHiRouterInterceptor 的缓存
     */
    private static final Cache<Class, EHiRouterInterceptor> interceptorClassCache =
            DefaultCacheFactory.INSTANCE.build(CacheType.ROUTER_INTERCEPTOR_CACHE);

    /**
     * 内部做了缓存,如果缓存中没有就会反射创建拦截器对象
     */
    @Nullable
    public static synchronized EHiRouterInterceptor getInterceptorByClass(
            @NonNull Class<? extends EHiRouterInterceptor> tClass) {
        EHiRouterInterceptor t = interceptorClassCache.get(tClass);
        if (t != null) {
            return t;
        }
        try {
            // 创建拦截器
            t = create(tClass);
            if (t == null) {
                throw new InstantiationException("do you write default constructor or a constructor with parameter 'Application' or  a constructor with parameter 'Context' ");
            } else {
                interceptorClassCache.put(tClass, t);
            }
        } catch (Exception e) {
            if (ComponentConfig.isDebug()) {
                throw new CreateInterceptorException(e);
            }
        }
        return t;
    }

    @Nullable
    private static EHiRouterInterceptor create(@NonNull Class<? extends EHiRouterInterceptor> tClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors = tClass.getConstructors();
        if (constructors == null) {
            return null;
        }
        // 这里为什么使用 for 循环而不是直接获取空参数的构造函数或者以下有某个参数的构造函数
        // 是因为你获取的时候会有异常抛出,三种情况你得 try{}catch{}三次
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length == 0) {
                return (EHiRouterInterceptor) constructor.newInstance();
            }
            if (parameterTypes.length == 1 && parameterTypes[0] == Application.class) {
                return (EHiRouterInterceptor) constructor.newInstance(ComponentConfig.getApplication());
            }
            if (parameterTypes.length == 1 && parameterTypes[0] == Context.class) {
                return (EHiRouterInterceptor) constructor.newInstance(ComponentConfig.getApplication());
            }
        }
        return null;
    }

    public static synchronized void removeCache(@NonNull Class<? extends EHiRouterInterceptor> tClass) {
        interceptorClassCache.remove(tClass);
    }

}
