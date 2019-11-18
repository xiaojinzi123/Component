package com.xiaojinzi.component;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.support.Inject;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.Utils;

/**
 * 组件化类,需要被初始化
 * 组件化的配置类,可以拿到 Application
 * time   : 2018/08/09
 *
 * @author : xiaojinzi 30212
 */
public class Component {

    /**
     * 是否初始化过了
     */
    private static boolean isInit = false;

    /**
     * 是否是 debug 状态
     */
    private static boolean isDebug = false;

    /**
     * 全局的 Application
     */
    private static Application application = null;

    /**
     * 默认的 scheme
     */
    private static String defaultScheme = "router";

    /**
     * 初始化优化的开关.
     * 默认是 false, 初始化的时候采用反射的形式
     * 当是 true 的时候, 初始化的时候,
     */
    private static boolean isInitOptimize = false;

    /**
     * 当用户使用 Application 发起跳转的时候, 是否提醒它
     */
    private static boolean isTipWhenUseApplication = true;

    private Component() {
    }

    /**
     * 初始化
     *
     * @param application App 的 Application
     * @param isDebug     是否是debug模式
     */
    @MainThread
    public static void init(@NonNull Application application, boolean isDebug) {
        init(application, isDebug, null);
    }

    /**
     * 打开初始化优化的开关
     */
    @AnyThread
    public static void openInitOptimize() {
        if (!isInit) {
            throw new RuntimeException("you must init Component first");
        }
        isInitOptimize = true;
    }

    /**
     * 关闭使用 Application 的日志
     */
    @AnyThread
    public static void closeLogWhenUseApplication() {
        if (!isInit) {
            throw new RuntimeException("you must init Component first");
        }
        isTipWhenUseApplication = false;
    }

    /**
     * 初始化
     *
     * @param application App 的 Application
     * @param isDebug     是否是debug模式
     */
    @MainThread
    public static void init(@NonNull Application application, boolean isDebug, @Nullable String defaultScheme) {
        Utils.checkMainThread();
        if (isInit) {
            throw new RuntimeException("Component is already init");
        }
        if (application == null) {
            throw new NullPointerException("the Application is null");
        }
        Component.application = application;
        Component.isDebug = isDebug;
        if (defaultScheme != null && !defaultScheme.isEmpty()) {
            Component.defaultScheme = defaultScheme;
        }
        // 注册
        application.registerActivityLifecycleCallbacks(new ComponentLifecycleCallback());
        isInit = true;
    }

    /**
     * 返回是否是 debug 状态
     */
    @AnyThread
    public static boolean isDebug() {
        return isDebug;
    }

    /**
     * 返回是否开启初始化优化
     */
    @AnyThread
    public static boolean isInitOptimize() {
        return isInitOptimize;
    }

    /**
     * 返回是否打印地址当使用 Application 的时候
     */
    @AnyThread
    public static boolean isLogWhenUseApplication() {
        return isTipWhenUseApplication;
    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    @NonNull
    @AnyThread
    public static Application getApplication() {
        if (application == null) {
            throw new NullPointerException("the Application is null,do you call Component.init(Application application,boolean isDebug)?");
        }
        return application;
    }

    /**
     * 获取默认的 scheme
     *
     * @return
     */
    public static String getDefaultScheme() {
        return defaultScheme;
    }

    /**
     * 找到实现类,执行注入
     *
     * @param target 目标界面
     */
    @MainThread
    public static void inject(@NonNull Object target) {
        injectFromBundle(target, null);
    }

    /**
     * 找到实现类,执行注入
     *
     * @param target 目标界面
     */
    @MainThread
    public static void injectFromIntent(@NonNull Object target, @Nullable Intent intent) {
        injectFromBundle(target, intent == null ? null : intent.getExtras());
    }

    /**
     * 找到实现类,执行注入
     *
     * @param target 目标界面
     */
    @MainThread
    public static void injectFromBundle(@NonNull Object target, @Nullable Bundle bundle) {
        Utils.checkMainThread();
        Utils.checkNullPointer(target, "target");
        String injectClassName = target.getClass().getName() + ComponentConstants.INJECT_SUFFIX;
        try {
            Class<?> targetInjectClass = Class.forName(injectClassName);
            Inject inject = (Inject) targetInjectClass.newInstance();
            if (bundle == null) {
                inject.inject(target);
            } else {
                inject.inject(target, bundle);
            }
        } catch (Exception ignore) {
            LogUtil.log(target.getClass().getName(), "field inject fail");
        }
    }

}
