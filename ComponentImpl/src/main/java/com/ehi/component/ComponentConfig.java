package com.ehi.component;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * 组件化的配置类
 * time   : 2018/08/09
 *
 * @author : xiaojinzi 30212
 */
public class ComponentConfig {

    private static boolean isDebug = false;
    private static Application application = null;

    public static void init(@NonNull Application application, boolean isDebug) {
        if (application == null) {
            throw new NullPointerException("the Application is null");
        }
        ComponentConfig.application = application;
        ComponentConfig.isDebug = isDebug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static Application getApplication() {
        if (application == null) {
            throw new NullPointerException("the Application is null,do you call ComponentConfig.init(Application application,boolean isDebug)?");
        }
        return application;
    }

}
