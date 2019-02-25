package com.ehi.component.impl.interceptor;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.cache.Cache;
import com.ehi.component.cache.CacheType;
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
    private static final Cache<Class, EHiRouterInterceptor> interceptorClassCache = Cache.Factory.INSTANCE.build
            (CacheType.ROUTER_INTERCEPTOR_CACHE);
    /**
     * 拦截器 Name(String) --> EHiRouterInterceptor 的缓存
     */
    private static final Cache<String, EHiRouterInterceptor> interceptorNameCache = Cache.Factory.INSTANCE.build
            (CacheType.ROUTER_INTERCEPTOR_CACHE);


    /**
     * 内部做了缓存,如果缓存中没有就会反射创建拦截器对象
     *
     * @param tClass
     * @return
     */
    @Nullable
    public static synchronized EHiRouterInterceptor getInterceptorByClass(@NonNull Class<? extends EHiRouterInterceptor> tClass) {
        EHiRouterInterceptor t = interceptorClassCache.get(tClass);
        if (t != null) {
            return t;
        }
        try {
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
        // 空参数构造函数
        Constructor<? extends EHiRouterInterceptor> constructor1 = tClass.getConstructor();
        if (constructor1 != null) {
            return constructor1.newInstance();
        }
        Constructor<? extends EHiRouterInterceptor> constructor2 = tClass.getConstructor(Application.class);
        if (constructor2 != null) {
            return constructor2.newInstance(ComponentConfig.getApplication());
        }
        Constructor<? extends EHiRouterInterceptor> constructor3 = tClass.getConstructor(Context.class);
        if (constructor3 != null) {
            return constructor3.newInstance(ComponentConfig.getApplication());
        }
        return null;
    }

    public static synchronized void removeCache(@NonNull Class<? extends EHiRouterInterceptor> tClass) {
        interceptorClassCache.remove(tClass);
    }

    /**
     * 根据拦截器的名字获取拦截器的缓存对象
     *
     * @param interceptorName
     * @return
     */
    @Nullable
    public static synchronized EHiRouterInterceptor getInterceptorByName(@NonNull String interceptorName) {
        return interceptorNameCache.get(interceptorName);
    }

    public static synchronized void putCache(@NonNull String interceptorName, @NonNull EHiRouterInterceptor interceptor) {
        interceptorNameCache.put(interceptorName, interceptor);
    }

    public static synchronized void removeCache(@NonNull String interceptorName) {
        interceptorNameCache.remove(interceptorName);
    }

}
