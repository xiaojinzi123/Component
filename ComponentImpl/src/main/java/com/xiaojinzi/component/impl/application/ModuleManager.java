package com.xiaojinzi.component.impl.application;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.Config;
import com.xiaojinzi.component.application.IComponentCenterApplication;
import com.xiaojinzi.component.application.IComponentHostApplication;
import com.xiaojinzi.component.impl.service.ServiceManager;
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
        Utils.checkNullPointer(moduleApp);
        if (moduleApplicationMap.containsKey(moduleApp.getHost())) {
            LogUtil.loge("The module \"" + moduleApp.getHost() + "\" is already registered");
        } else {
            moduleApplicationMap.put(moduleApp.getHost(), moduleApp);
            moduleApp.onCreate(Component.getApplication());
            notifyModuleChanged();
        }
    }

    @Override
    public void register(@NonNull String host) {
        Utils.checkNullPointer(host, "host");
        if (moduleApplicationMap.containsKey(host)) {
            LogUtil.loge("the host '" + host + "' is already load");
            return;
        } else {
            IComponentHostApplication moduleApplication = findModuleApplication(host);
            if (moduleApplication == null) {
                LogUtil.log("模块 '" + host + "' 加载失败, 请根据链接中的内容自行排查! \n " + Component.COMMON_ERROR_ISSUE);
            } else {
                register(moduleApplication);
            }
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
                register(host);
            }
        }
    }

    @Override
    public void unregister(@NonNull IComponentHostApplication moduleApp) {
        Utils.checkNullPointer(moduleApp);
        moduleApplicationMap.remove(moduleApp.getHost());
        moduleApp.onDestroy();
        notifyModuleChanged();
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
            result = ASMUtil.findModuleApplicationAsmImpl(
                    ComponentUtil.transformHostForClass(host)
            );
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

    @UiThread
    private void notifyModuleChanged() {
        final int compareValue = Utils.COUNTER.incrementAndGet();
        Utils.postDelayActionToMainThread(new Runnable() {
            @Override
            public void run() {
                // 说明没有改变过
                if (compareValue == Utils.COUNTER.get()) {
                    // LogUtil.loge("通知 " + compareValue);
                    doNotifyModuleChanged();
                } else {
                    // LogUtil.loge("放弃通知 " + compareValue);
                }
            }
        }, Component.getConfig().getNotifyModuleChangedDelayTime());
    }

    @UiThread
    private void doNotifyModuleChanged() {
        for (IComponentHostApplication hostApplication : moduleApplicationMap.values()) {
            hostApplication.onModuleChanged(Component.getApplication());
        }
        // 触发自动初始化
        Utils.postActionToWorkThread(new Runnable() {
            @Override
            public void run() {
                ServiceManager.autoInitService();
            }
        });
    }

    /**
     * 请使用 {@link Component#check()}
     *
     * @deprecated 未来版本会删除
     */
    @Deprecated
    public void check() {
        Component.check();
    }

}
