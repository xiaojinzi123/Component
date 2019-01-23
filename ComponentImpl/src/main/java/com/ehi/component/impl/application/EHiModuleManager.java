package com.ehi.component.impl.application;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;
import com.ehi.component.ComponentUtil;
import com.ehi.component.application.IComponentHostApplication;
import com.ehi.component.application.IComponentModuleApplication;
import com.ehi.component.bean.EHiRouterBean;
import com.ehi.component.impl.EHiRouterCenter;
import com.ehi.component.impl.interceptor.EHiCenterInterceptor;
import com.ehi.component.interceptor.IComponentHostInterceptor;
import com.ehi.component.router.IComponentHostRouter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 这个类必须放在 {@link ComponentUtil#IMPL_OUTPUT_PKG} 包下面
 * 这是是管理每一个模块之前联系的管理类,加载模块的功能也是这个类负责的
 */
public class EHiModuleManager implements IComponentModuleApplication {

    private static volatile EHiModuleManager instance;

    private static Map<String, IComponentHostApplication> moduleApplicationMap = new HashMap<>();

    private EHiModuleManager() {
    }

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
        IComponentHostApplication moduleApplication = findModuleApplication(host);
        register(moduleApplication);
    }

    public void registerArr(@NonNull String... hosts) {
        if (hosts != null) {
            for (String host : hosts) {
                IComponentHostApplication moduleApplication = findModuleApplication(host);
                register(moduleApplication);
            }
        }
    }

    @Override
    public void unregister(IComponentHostApplication moduleApp) {
        unregister(moduleApp.getHost());
    }

    @Override
    public void unregister(@NonNull String host) {

        IComponentHostApplication moduleApp = moduleApplicationMap.remove(host);
        if (moduleApp == null) {
            return;
        }

        moduleApp.onDestory();

    }

    @Nullable
    public static IComponentHostApplication findModuleApplication(String host) {
        String className = ComponentUtil.genHostModuleApplicationClassName(host);
        try {
            Class<?> clazz = Class.forName(className);
            /*Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);*/
            IComponentHostApplication instance = (IComponentHostApplication) clazz.newInstance();
            return instance;
        } catch (ClassNotFoundException e) {
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        }
        return null;
    }

    /**
     * 使用者应该在开发阶段调用这个函数来检查以下的问题：
     * 1.路由表在不同的子路由表中是否有重复
     * 2.服务在不同模块中的声明是否也有重复的名称
     */
    public void check() {
        checkRouter();
        checkInterceptor();
    }

    private void checkRouter() {
        if (moduleApplicationMap == null || moduleApplicationMap.isEmpty()) {
            return;
        }
        Set<String> set = new HashSet<>();
        for (String moduleName : moduleApplicationMap.keySet()) {
            IComponentHostRouter routerImpl = EHiRouterCenter.getInstance().findUiRouter(moduleName);
            if (routerImpl == null) {
                continue;
            }
            Map<String, EHiRouterBean> routerMap = routerImpl.getRouterMap();
            if (routerMap == null) {
                continue;
            }
            Set<String> routerUrlSet = routerMap.keySet();
            for (String url : routerUrlSet) {
                if (set.contains(url)) {
                    throw new RuntimeException("the target url is exist：" + url);
                }
                set.add(url);
            }
        }
    }

    private void checkInterceptor() {
        Set<String> set = new HashSet<>();
        for (String moduleName : moduleApplicationMap.keySet()) {
            IComponentHostInterceptor moduleInterceptor = EHiCenterInterceptor.getInstance().findModuleInterceptor(moduleName);
            if (moduleInterceptor == null) {
                continue;
            }
            Set<String> interceptorNames = moduleInterceptor.getInterceptorNames();
            if (interceptorNames == null) {
                continue;
            }
            for (String interceptorName : interceptorNames) {
                if (set.contains(interceptorName)) {
                    throw new RuntimeException("the interceptor's name is exist：" + interceptorName);
                }
                set.add(interceptorName);
            }

        }
    }

}
