package com.xiaojinzi.component.impl.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.ComponentConfig;
import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.service.IComponentCenterService;
import com.xiaojinzi.component.service.IComponentHostService;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块服务注册和卸载的总管
 *
 * @author xiaojinzi 30212
 */
public class ServiceCenter implements IComponentCenterService {

    private Map<String, IComponentHostService> moduleServiceMap = new HashMap<>();

    private ServiceCenter() {
    }

    private static volatile ServiceCenter instance;

    public static ServiceCenter getInstance() {
        if (instance == null) {
            synchronized (ServiceCenter.class) {
                if (instance == null) {
                    instance = new ServiceCenter();
                }
            }
        }
        return instance;
    }

    @Override
    public void register(@Nullable IComponentHostService service) {
        if (service == null) {
            return;
        }
        moduleServiceMap.put(service.getHost(), service);
        service.onCreate(ComponentConfig.getApplication());
    }

    @Override
    public void register(@NonNull String host) {
        if (moduleServiceMap.containsKey(host)) {
            return;
        }
        IComponentHostService moduleService = findModuleService(host);
        register(moduleService);
    }

    @Override
    public void unregister(@Nullable IComponentHostService moduleService) {
        if (moduleService == null) {
            return;
        }
        moduleService.onDestory();
    }

    @Override
    public void unregister(@NonNull String host) {
        IComponentHostService moduleService = moduleServiceMap.remove(host);
        unregister(moduleService);
    }

    @Nullable
    public IComponentHostService findModuleService(String host) {
        String className = ComponentUtil.genHostServiceClassName(host);
        try {
            Class<?> clazz = Class.forName(className);
            return (IComponentHostService) clazz.newInstance();
        } catch (Exception ignore) {
            // ignore
        }
        return null;
    }

}