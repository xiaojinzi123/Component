package com.ehi.component.impl.interceptor;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouterInterceptor;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 拦截器 Class --> EHiRouterInterceptor 的缓存
     */
    private static final Map<Class,EHiRouterInterceptor> interceptorClassCacheMap = new HashMap<>();

    /**
     * 拦截器 Name(String) --> EHiRouterInterceptor 的缓存
     */
    private static final Map<String,EHiRouterInterceptor> interceptorNameCacheMap = new HashMap<>();

    /**
     * 内部做了缓存,如果缓存中没有就会反射创建拦截器对象
     *
     * @param tClass
     * @return
     */
    @Nullable
    public static synchronized EHiRouterInterceptor getInterceptorByClass(@NonNull Class<? extends EHiRouterInterceptor> tClass) {
        EHiRouterInterceptor t = interceptorClassCacheMap.get(tClass);
        if (t == null) {
            try {
                Constructor<?>[] constructors = tClass.getConstructors();
                if (constructors != null) {
                    for (Constructor<?> constructor : constructors) {
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        if (parameterTypes == null || parameterTypes.length == 0) {
                            t = (EHiRouterInterceptor) constructor.newInstance();
                            break;
                        }
                        if (parameterTypes != null && parameterTypes.length == 1 && parameterTypes[0] == Application.class) {
                            t = (EHiRouterInterceptor) constructor.newInstance(ComponentConfig.getApplication());
                            break;
                        }
                        if (parameterTypes != null && parameterTypes.length == 1 && parameterTypes[0] == Context.class) {
                            t = (EHiRouterInterceptor) constructor.newInstance(ComponentConfig.getApplication());
                            break;
                        }
                    }
                    if (t == null) {
                        throw new InstantiationException("do you write default constructor or a constructor with parameter 'Application' or  a constructor with parameter 'Context' ");
                    } else {
                        interceptorClassCacheMap.put(tClass, t);
                    }
                }
            } catch (Exception e) {
                if (ComponentConfig.isDebug()) {
                    throw new RuntimeException(e);
                }
            }
        }
        return t;
    }

    public static synchronized void removeCache(@NonNull Class<? extends EHiRouterInterceptor> tClass) {
        interceptorClassCacheMap.remove(tClass);
    }

    /**
     * 根据拦截器的名字获取拦截器的缓存对象
     *
     * @param interceptorName
     * @return
     */
    @Nullable
    public static synchronized EHiRouterInterceptor getInterceptorByName(@NonNull String interceptorName) {
        return interceptorNameCacheMap.get(interceptorName);
    }

    public static synchronized void putCache(@NonNull String interceptorName, @NonNull EHiRouterInterceptor interceptor) {
        interceptorNameCacheMap.put(interceptorName, interceptor);
    }

    public static synchronized void removeCache(@NonNull String interceptorName) {
        interceptorNameCacheMap.remove(interceptorName);
    }

}
