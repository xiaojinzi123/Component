package com.xiaojinzi.component.support;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.cache.ClassCache;
import com.xiaojinzi.component.error.CreateInterceptorException;
import com.xiaojinzi.component.impl.RouterDegrade;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 支持缓存自定义的降级处理工具类
 *
 * @author : xiaojinzi 30212
 */
public class RouterDegradeCache {

    private RouterDegradeCache() {
    }

    /**
     * 内部做了缓存,如果缓存中没有就会反射创建拦截器对象
     */
    @Nullable
    public static synchronized RouterDegrade getRouterDegradeByClass(@NonNull Class<? extends RouterDegrade> tClass) {
        RouterDegrade t = ClassCache.get(tClass);
        if (t != null) {
            return t;
        }
        try {
            // 创建拦截器
            t = create(tClass);
            if (t == null) {
                throw new InstantiationException("do you write default constructor or a constructor with parameter 'Application' or  a constructor with parameter 'Context' ");
            } else {
                ClassCache.put(tClass, t);
            }
        } catch (Exception e) {
            if (Component.isDebug()) {
                throw new CreateInterceptorException(e);
            }
        }
        return t;
    }

    @Nullable
    private static RouterDegrade create(@NonNull Class<? extends RouterDegrade> tClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors = tClass.getConstructors();
        if (constructors == null) {
            return null;
        }
        // 这里为什么使用 for 循环而不是直接获取空参数的构造函数或者以下有某个参数的构造函数
        // 是因为你获取的时候会有异常抛出,三种情况你得 try{}catch{}三次
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length == 0) {
                return (RouterDegrade) constructor.newInstance();
            }
            if (parameterTypes.length == 1 && parameterTypes[0] == Application.class) {
                return (RouterDegrade) constructor.newInstance(Component.getApplication());
            }
            if (parameterTypes.length == 1 && parameterTypes[0] == Context.class) {
                return (RouterDegrade) constructor.newInstance(Component.getApplication());
            }
        }
        return null;
    }

    public static synchronized void removeCache(@NonNull Class<? extends RouterDegrade> tClass) {
        ClassCache.remove(tClass);
    }

}
