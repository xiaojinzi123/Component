package com.ehi.component.impl.application;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;
import com.ehi.component.ComponentUtil;
import com.ehi.component.application.IComponentHostApplication;
import com.ehi.component.application.IComponentModuleApplication;
import com.ehi.component.impl.EHiRouterCenter;
import com.ehi.component.impl.interceptor.EHiInterceptorCenter;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类必须放在 {@link ComponentUtil#IMPL_OUTPUT_PKG} 包下面
 * 这是是管理每一个模块之前联系的管理类,加载模块的功能也是这个类负责的
 *
 * @author xiaojinzi 30212
 */
public class EHiModuleManager implements IComponentModuleApplication {

    /**
     * 单例对象
     */
    private static volatile EHiModuleManager instance;

    private EHiModuleManager() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static EHiModuleManager getInstance() {
        if (instance == null) {
            synchronized (EHiModuleManager.class) {
                if (instance == null) {
                    instance = new EHiModuleManager();
                }
            }
        }
        return instance;
    }

    private static Map<String, IComponentHostApplication> moduleApplicationMap = new HashMap<>();

    @Override
    public void register(IComponentHostApplication moduleApp) {
        if (moduleApp == null) {
            return;
        }
        moduleApplicationMap.put(moduleApp.getHost(), moduleApp);
        moduleApp.onCreate(ComponentConfig.getApplication());
    }

    @Override
    public void register(@NonNull String host) {
        if (moduleApplicationMap.containsKey(host)) {
            return;
        }
        IComponentHostApplication moduleApplication = findModuleApplication(host);
        register(moduleApplication);
    }

    public void registerArr(@Nullable String... hosts) {
        if (hosts != null) {
            for (String host : hosts) {
                IComponentHostApplication moduleApplication = findModuleApplication(host);
                register(moduleApplication);
            }
        }
    }

    @Override
    public void unregister(IComponentHostApplication moduleApp) {
        if (moduleApp == null) {
            return;
        }
        moduleApp.onDestory();
    }

    @Override
    public void unregister(@NonNull String host) {
        IComponentHostApplication moduleApp = moduleApplicationMap.remove(host);
        unregister(moduleApp);
    }

    @Nullable
    public static IComponentHostApplication findModuleApplication(String host) {
        String className = ComponentUtil.genHostModuleApplicationClassName(host);
        try {
            Class<?> clazz = Class.forName(className);
            return (IComponentHostApplication) clazz.newInstance();
        } catch (Exception ignore) {
            // ignore
        }
        return null;
    }

    /**
     * 使用者应该在开发阶段调用这个函数来检查以下的问题：
     * 1.路由表在不同的子路由表中是否有重复
     * 2.服务在不同模块中的声明是否也有重复的名称
     */
    public void check() {
        EHiRouterCenter.getInstance().check();
        EHiInterceptorCenter.getInstance().check();
    }

}
