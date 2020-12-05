package com.xiaojinzi.component.impl.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.fragment.IComponentCenterFragment;
import com.xiaojinzi.component.fragment.IComponentHostFragment;
import com.xiaojinzi.component.support.ASMUtil;
import com.xiaojinzi.component.support.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 模块 Fragment 注册和卸载的总管
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
public class FragmentCenter implements IComponentCenterFragment {

    private Map<String, IComponentHostFragment> moduleFragmentMap = new HashMap<>();

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
    public void register(@NonNull IComponentHostFragment hostFragment) {
        Utils.checkNullPointer(hostFragment);
        if (!moduleFragmentMap.containsKey(hostFragment.getHost())) {
            moduleFragmentMap.put(hostFragment.getHost(), hostFragment);
            hostFragment.onCreate(Component.getApplication());
        }
    }

    @Override
    public void register(@NonNull String host) {
        Utils.checkStringNullPointer(host, "host");
        if (!moduleFragmentMap.containsKey(host)) {
            IComponentHostFragment moduleService = findModuleService(host);
            if (moduleService != null) {
                register(moduleService);
            }
        }
    }

    @Override
    public void unregister(@NonNull IComponentHostFragment hostFragment) {
        Utils.checkNullPointer(hostFragment);
        moduleFragmentMap.remove(hostFragment.getHost());
        hostFragment.onDestroy();
    }

    @Override
    public void unregister(@NonNull String host) {
        Utils.checkStringNullPointer(host, "host");
        IComponentHostFragment moduleService = moduleFragmentMap.get(host);
        if (moduleService != null) {
            unregister(moduleService);
        }
    }

    @Nullable
    public IComponentHostFragment findModuleService(String host) {
        try {
            if (Component.getConfig().isOptimizeInit()) {
                return ASMUtil.findModuleFragmentAsmImpl(
                        ComponentUtil.transformHostForClass(host)
                );
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

    public void check() {
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, IComponentHostFragment> entry : moduleFragmentMap.entrySet()) {
            IComponentHostFragment childRouter = entry.getValue();
            if (childRouter == null || childRouter.getFragmentNameSet() == null) {
                continue;
            }
            Set<String> childRouterSet = childRouter.getFragmentNameSet();
            for (String key : childRouterSet) {
                if (set.contains(key)) {
                    throw new IllegalStateException("the name of Fragment is exist：'" + key + "'");
                }
                set.add(key);
            }
        }
    }

}