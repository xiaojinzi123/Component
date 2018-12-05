package com.ehi.component.impl.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;
import com.ehi.component.ComponentUtil;
import com.ehi.component.service.IComponentHostService;
import com.ehi.component.service.IComponentModuleService;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块服务注册和卸载的总管
 */
public class EHiCenterService implements IComponentModuleService {

    private Map<String, IComponentHostService> moduleServiceMap = new HashMap<>();

    private EHiCenterService() {
    }

    private static volatile EHiCenterService instance;

    public static EHiCenterService getInstance() {
        if (instance == null) {
            synchronized (EHiCenterService.class) {
                if (instance == null) {
                    instance = new EHiCenterService();
                }
            }
        }
        return instance;
    }

    @Override
    public void register(@NonNull IComponentHostService service) {
        if (service == null) {
            return;
        }
        moduleServiceMap.put(service.getHost(), service);
        service.onCreate(ComponentConfig.getApplication());
    }

    @Override
    public void register(@NonNull String host) {
        IComponentHostService moduleService = findModuleService(host);
        register(moduleService);
    }

    @Override
    public void unregister(@NonNull IComponentHostService service) {
        unregister(service.getHost());
    }

    @Override
    public void unregister(@NonNull String host) {
        IComponentHostService moduleService = moduleServiceMap.remove(host);
        if (moduleService == null) {
            return;
        }
        moduleService.onDestory();
    }

    @Nullable
    public IComponentHostService findModuleService(String host) {

        String className = ComponentUtil.genHostServiceClassName(host);

        try {
            Class<?> clazz = Class.forName(className);

            IComponentHostService instance = (IComponentHostService) clazz.newInstance();

            return instance;

        } catch (ClassNotFoundException e) {
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        }

        return null;

    }

}