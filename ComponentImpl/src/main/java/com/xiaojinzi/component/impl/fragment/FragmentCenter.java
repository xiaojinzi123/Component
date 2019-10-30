package com.xiaojinzi.component.impl.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.anno.support.CheckClassName;
import com.xiaojinzi.component.fragment.IComponentCenterFragment;
import com.xiaojinzi.component.fragment.IComponentHostFragment;
import com.xiaojinzi.component.support.ASMUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块 Fragment 注册和卸载的总管
 *
 * @author xiaojinzi 30212
 */
@CheckClassName
public class FragmentCenter implements IComponentCenterFragment {

    private Map<String, IComponentHostFragment> moduleServiceMap = new HashMap<>();

    private FragmentCenter() {
    }

    private static volatile FragmentCenter instance;

    public static FragmentCenter getInstance() {
        if (instance == null) {
            synchronized (FragmentCenter.class) {
                if (instance == null) {
                    instance = new FragmentCenter();
                }
            }
        }
        return instance;
    }

    @Override
    public void register(@Nullable IComponentHostFragment service) {
        if (service == null) {
            return;
        }
        moduleServiceMap.put(service.getHost(), service);
        service.onCreate(Component.getApplication());
    }

    @Override
    public void register(@NonNull String host) {
        if (moduleServiceMap.containsKey(host)) {
            return;
        }
        IComponentHostFragment moduleService = findModuleService(host);
        register(moduleService);
    }

    @Override
    public void unregister(@Nullable IComponentHostFragment moduleService) {
        if (moduleService == null) {
            return;
        }
        moduleServiceMap.remove(moduleService.getHost());
        moduleService.onDestroy();
    }

    @Override
    public void unregister(@NonNull String host) {
        IComponentHostFragment moduleService = moduleServiceMap.get(host);
        unregister(moduleService);
    }

    @Nullable
    public IComponentHostFragment findModuleService(String host) {
        try {
            if (Component.isInitOptimize()) {
                return ASMUtil.findModuleFragmentAsmImpl(host);
            } else {
                Class<? extends IComponentHostFragment> clazz = null;
                String className = ComponentUtil.genHostFragmentClassName(host);
                clazz = (Class<? extends IComponentHostFragment>) Class.forName(className);
                return clazz.newInstance();
            }
        } catch (Exception ignore) {
            // ignore
        }
        return null;
    }

}