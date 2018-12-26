package com.ehi.component.impl.interceptor;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;
import com.ehi.component.anno.EHiRouterAnno;

import java.lang.reflect.Constructor;
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
public class EHiRouterInterceptorUtil {

    private static final Map map = new HashMap<>();

    @Nullable
    public static synchronized <T> T get(@NonNull Class<T> tClass) {

        T t = (T) map.get(tClass);

        if (t == null) {
            try {
                Constructor<?>[] constructors = tClass.getConstructors();
                if (constructors != null) {
                    for (Constructor<?> constructor : constructors) {
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        if (parameterTypes == null || parameterTypes.length == 0) {
                            t = (T) constructor.newInstance();
                            break;
                        }
                        if (parameterTypes != null && parameterTypes.length == 1 && parameterTypes[0] == Application.class) {
                            t = (T)constructor.newInstance(ComponentConfig.getApplication());
                            break;
                        }
                        if (parameterTypes != null && parameterTypes.length == 1 && parameterTypes[0] == Context.class) {
                            t = (T)constructor.newInstance(ComponentConfig.getApplication());
                            break;
                        }
                    }
                    if (t == null) {
                        throw new InstantiationException("do you write default constructor or a constructor with parameter 'Application' or  a constructor with parameter 'Context' ");
                    }else {
                        map.put(tClass, t);
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

}
