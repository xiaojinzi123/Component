package com.ehi.component;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * 组件化的配置类,可以拿到 Application
 * time   : 2018/08/09
 *
 * @author : xiaojinzi 30212
 */
public class ComponentConfig {

    private static boolean isDebug = false;
    private static Application application = null;

    /**
     * 初始化
     *
     * @param application App 的 Application
     * @param isDebug     是否是debug模式
     */
    public static void init(@NonNull Application application, boolean isDebug) {
        if (application == null) {
            throw new NullPointerException("the Application is null");
        }
        ComponentConfig.application = application;
        ComponentConfig.isDebug = isDebug;
        // 注册
        application.registerActivityLifecycleCallbacks(new ComponentLifecycleCallback());
    }

    public static boolean isDebug() {
        return isDebug;
    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    @NonNull
    public static Application getApplication() {
        if (application == null) {
            throw new NullPointerException("the Application is null,do you call ComponentConfig.init(Application application,boolean isDebug)?");
        }
        return application;
    }

}
