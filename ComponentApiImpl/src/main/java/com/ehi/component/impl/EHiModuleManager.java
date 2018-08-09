package com.ehi.component.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.EHIComponentUtil;
import com.ehi.component.application.IComponentHostApplication;
import com.ehi.component.application.IComponentModuleApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类必须放在 {@link com.ehi.component.EHIComponentUtil#IMPL_OUTPUT_PKG} 包下面
 */
public class EHiModuleManager implements IComponentModuleApplication {

    private static volatile EHiModuleManager instance;

    private static Map<String, IComponentHostApplication> moduleApplicationMap = new HashMap<>();

    @NonNull
    private static Context mApplication;

    public static void init(Context application) {
        mApplication = application;
        if (mApplication == null) {
            throw new NullPointerException("parameter 'Application' is null");
        }
    }

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

        moduleApp.onCreate(mApplication);

    }

    @Override
    public void register(@NonNull String host) {
        IComponentHostApplication moduleApplication = findModuleApplication(host);
        register(moduleApplication);
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

        String className = EHIComponentUtil.genHostModuleApplicationClassName(host);

        try {
            Class<?> clazz = Class.forName(className);

            IComponentHostApplication instance = (IComponentHostApplication) clazz.newInstance();

            return instance;

        } catch (ClassNotFoundException e) {
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        }

        return null;

    }

}
