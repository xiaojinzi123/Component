package com.xiaojinzi.component;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.support.Inject;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.Utils;

/**
 * 组件化类,需要被初始化
 * 组件化的配置类,可以拿到 Application
 * time   : 2018/08/09
 *
 * @author : xiaojinzi
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
     * 配置对象
     */
    private static Config mConfig = null;


    private Component() {
    }

    /**
     * 初始化
     */
    @MainThread
    public static void init(boolean isDebug, @NonNull Config config) {
        if (isInit) {
            throw new RuntimeException("you have init Component already!");
        }
        Utils.checkMainThread();
        Utils.checkNullPointer(config, "config");
        Component.isDebug = isDebug;
        mConfig = config;
        // 注册
        mConfig.getApplication().registerActivityLifecycleCallbacks(new ComponentLifecycleCallback());
        isInit = true;
    }

    @NonNull
    @AnyThread
    public static Config getConfig(){
        return mConfig;
    }

    /**
     * 返回是否是 debug 状态
     */
    @AnyThread
    public static boolean isDebug() {
        return Component.isDebug;
    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    @NonNull
    @AnyThread
    public static Application getApplication() {
        checkInit();
        return mConfig.getApplication();
    }

    private static void checkInit(){
        if (mConfig == null) {
            throw new RuntimeException("you must init Component first!");
        }
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
