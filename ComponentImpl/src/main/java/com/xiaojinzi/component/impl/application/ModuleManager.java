package com.xiaojinzi.component.impl.application;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.Config;
import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.application.IComponentCenterApplication;
import com.xiaojinzi.component.application.IComponentHostApplication;
import com.xiaojinzi.component.impl.RouterCenter;
import com.xiaojinzi.component.impl.fragment.FragmentCenter;
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter;
import com.xiaojinzi.component.support.ASMUtil;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 这个类必须放在 {@link ComponentUtil#IMPL_OUTPUT_PKG} 包下面
 * 这是是管理每一个模块之前联系的管理类,加载模块的功能也是这个类负责的
 *
 * @author xiaojinzi
 */
public class ModuleManager implements IComponentCenterApplication {

    public static final String ISSUE = "https://github.com/xiaojinzi123/Component/issues/21";

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
    public void register(@NonNull IComponentHostApplication moduleApp) {
        if (moduleApplicationMap.containsKey(moduleApp.getHost())) {
            LogUtil.loge("The module \"" + moduleApp.getHost() + "\" is already registered");
        }
        moduleApplicationMap.put(moduleApp.getHost(), moduleApp);
        moduleApp.onCreate(Component.getApplication());
    }

    @Override
    public void register(@NonNull String host) {
        if (host == null || "".equals(host)) {
            throw new RuntimeException("the host can't be null or empty");
        }
        if (moduleApplicationMap.containsKey(host)) {
            LogUtil.log("the host '" + host + "' is already load");
            return;
        }
        IComponentHostApplication moduleApplication = findModuleApplication(host);
        if (moduleApplication == null) {
            LogUtil.log("模块 '" + host + "' 加载失败");
        } else {
            register(moduleApplication);
        }
    }

    /**
     * 自动注册, 需要开启 {@link Config.Builder#optimizeInit(boolean)}
     * 表示使用 Gradle 插件优化初始化
     */
    public void autoRegister() {
        if (!Component.getConfig().isOptimizeInit()) {
            LogUtil.logw("you can't use this method to register module. Because you not turn on 'optimizeInit' by calling method 'Config.Builder.optimizeInit(true)' when you init");
        }
        List<String> moduleNames = ASMUtil.getModuleNames();
        if (moduleNames != null && !moduleNames.isEmpty()) {
            registerArr(moduleNames.toArray(new String[0]));
        }
    }

    /**
     * 注册业务模块, 可以传多个名称
     *
     * @param hosts host 的名称数组
     */
    public void registerArr(@Nullable String... hosts) {
        if (hosts != null) {
            for (String host : hosts) {
                IComponentHostApplication moduleApplication = findModuleApplication(host);
                if (moduleApplication == null) {
                    LogUtil.log("模块 '" + host + "' 加载失败, 请根据链接中的内容自行排查! \n " + ISSUE);
                } else {
                    register(moduleApplication);
                }
            }
        }
    }

    @Override
    public void unregister(@Nullable IComponentHostApplication moduleApp) {
        if (moduleApp == null) {
            return;
        }
        moduleApplicationMap.remove(moduleApp.getHost());
        moduleApp.onDestroy();
    }

    @Override
    public void unregister(@NonNull String host) {
        Utils.checkNullPointer(host, "host");
        IComponentHostApplication moduleApp = moduleApplicationMap.get(host);
        if (moduleApp == null) {
            LogUtil.log("模块 '" + host + "' 卸载失败");
        } else {
            unregister(moduleApp);
        }
    }

    public void unregisterAll() {
        // 创建一个 HashSet 是为了能循环的时候删除集合中的元素
        for (String host : new HashSet<>(moduleApplicationMap.keySet())) {
            unregister(host);
        }
    }

    @Nullable
    public static IComponentHostApplication findModuleApplication(@NonNull String host) {
        IComponentHostApplication result = null;
        if (Component.getConfig().isOptimizeInit()) {
            LogUtil.log("\"" + host + "\" will try to load by bytecode");
            result = ASMUtil.findModuleApplicationAsmImpl(host);
        } else {
            LogUtil.log("\"" + host + "\" will try to load by reflection");
            if (result == null) {
                try {
                    // 先找正常的
                    Class<?> clazz = Class.forName(ComponentUtil.genHostModuleApplicationClassName(host));
                    result = (IComponentHostApplication) clazz.newInstance();
                } catch (Exception ignore) {
                    // ignore
                }
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
        FragmentCenter.getInstance().check();
        // Service 不需要检查, 反正如果重复了就覆盖. 没得选
    }

}
