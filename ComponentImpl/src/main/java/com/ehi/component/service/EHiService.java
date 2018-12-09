package com.ehi.component.service;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;
import com.ehi.component.ComponentUtil;
import com.ehi.component.application.IComponentHostApplication;
import com.ehi.component.impl.EHiModuleManager;

import java.util.HashMap;
import java.util.Map;

/**
 * EHi 服务的容器
 */
public class EHiService {

    private static Map<Class, IServiceLoad<?>> map = new HashMap();

    /**
     * 你可以注册一个服务,服务的初始化可以是 懒加载的
     *
     * @param tClass
     * @param iServiceLoad
     * @param <T>
     */
    public static <T> void register(@NonNull Class<T> tClass, @NonNull IServiceLoad<? extends T> iServiceLoad) {
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
