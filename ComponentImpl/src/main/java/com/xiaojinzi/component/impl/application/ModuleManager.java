package com.xiaojinzi.component.impl.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.application.IComponentHostApplication;
import com.xiaojinzi.component.application.IComponentModuleApplication;
import com.xiaojinzi.component.impl.RouterCenter;
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类必须放在 {@link ComponentUtil#IMPL_OUTPUT_PKG} 包下面
 * 这是是管理每一个模块之前联系的管理类,加载模块的功能也是这个类负责的
 *
 * @author xiaojinzi 30212
 */
public class ModuleManager implements IComponentModuleApplication {

    /**
     * 单例对象
     */
    private static volatile ModuleManager instance;

    private ModuleManager() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static ModuleManager getInstance() {
        if (instance == null) {
            synchronized (ModuleManager.class) {
                if (instance == null) {
                    instance = new ModuleManager();
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
        moduleApp.onCreate(Component.getApplication());
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
        IComponentHostApplication result = null;
        try {
            // 先找正常的
            Class<?> clazz = Class.forName(ComponentUtil.genHostModuleApplicationClassName(host));
            result = (IComponentHostApplication) clazz.newInstance();
        } catch (Exception ignore) {
            // ignore
        }
        if (result == null) {
            try {
                // 找默认的
                Class<?> clazz = Class.forName(ComponentUtil.genDefaultHostModuleApplicationClassName(host));
                result = (IComponentHostApplication) clazz.newInstance();
            } catch (Exception ignore) {
                // ignore
            }
        }
        return result;
    }

    /**
     * 使用者应该在开发阶段调用这个函数来检查以下的问题：
     * 1.路由表在不同的子路由表中是否有重复
     * 2.服务在不同模块中的声明是否也有重复的名称
     */
    public void check() {
        RouterCenter.getInstance().check();
        InterceptorCenter.getInstance().check();
    }

}
