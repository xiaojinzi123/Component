package com.xiaojinzi.component;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.impl.application.ModuleManager;
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
     *
     * @see Config 初始化的配置对象
     */
    @MainThread
    public static void init(boolean isDebug, @NonNull Config config) {

        // 做必要的检查
        if (isInit) {
            throw new RuntimeException("you have init Component already!");
        }
        Utils.checkMainThread();
        Utils.checkNullPointer(config, "config");

        Component.isDebug = isDebug;
        mConfig = config;
        // 注册
        mConfig.getApplication().registerActivityLifecycleCallbacks(new ComponentLifecycleCallback());
        if (mConfig.isOptimizeInit() && mConfig.isAutoRegisterModule()) {
            ModuleManager.getInstance().autoRegister();
        }
        isInit = true;
        if (isDebug) {
            printComponent();
        }

    }

    /**
     * 打印宣传内容和 logo
     */
    private static void printComponent() {
        StringBuffer sb = new StringBuffer();
        sb.append(" \n");

        // 打印logo C

        sb.append("\n");
        sb.append("             *********\n");
        sb.append("          ****        ****\n");
        sb.append("       ****              ****\n");
        sb.append("     ****\n");
        sb.append("    ****\n");
        sb.append("    ****\n");
        sb.append("    ****\n");
        sb.append("     ****\n");
        sb.append("       ****              ****\n");
        sb.append("          ****        ****\n");
        sb.append("             *********\n");

        sb.append("感谢您选择 Component 组件化框架. \n有任何问题欢迎提 issue 或者扫描 github 上的二维码进入群聊@群主\n")
                .append("Github 地址：https://github.com/xiaojinzi123/Component")
                .append("\n文档地址：https://github.com/xiaojinzi123/Component/wiki")
                .append("\n ");

        LogUtil.logw(sb.toString());
    }

    @NonNull
    @AnyThread
    public static Config getConfig() {
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

    private static void checkInit() {
        if (mConfig == null) {
            throw new RuntimeException("you must init Component first!");
        }
    }

    @MainThread
    public static void inject(@NonNull Object target) {
        inject(target, null, true, true);
    }

    @MainThread
    public static void injectAttrValueFromIntent(@NonNull Object target, @Nullable Intent intent) {
        injectAttrValueFromBundle(target, intent == null ? null : intent.getExtras());
    }

    @MainThread
    public static void injectAttrValueFromBundle(@NonNull Object target, @Nullable Bundle bundle) {
        inject(target, bundle, true, false);
    }

    @MainThread
    public static void injectService(@NonNull Object target) {
        inject(target, null, false, true);
    }

    /**
     * 注入功能
     *
     * @param target              目标, 可能是任意的类
     * @param bundle              属性注入的 Bundle 数据提供者
     * @param isAutoWireAttrValue 是否注入属性值
     * @param isAutoWireService   是否注入 Service
     */
    @MainThread
    private static void inject(@NonNull Object target, @Nullable Bundle bundle,
                               boolean isAutoWireAttrValue,
                               boolean isAutoWireService) {
        Utils.checkMainThread();
        Utils.checkNullPointer(target, "target");
        String injectClassName = target.getClass().getName() + ComponentConstants.INJECT_SUFFIX;
        try {
            Class<?> targetInjectClass = Class.forName(injectClassName);
            Inject inject = (Inject) targetInjectClass.newInstance();
            if (isAutoWireService) {
                inject.injectService(target);
            }
            if (isAutoWireAttrValue) {
                if (bundle == null) {
                    inject.injectAttrValue(target);
                } else {
                    Utils.checkNullPointer(bundle, "bundle");
                    inject.injectAttrValue(target, bundle);
                }
            }
        } catch (Exception ignore) {
            LogUtil.log("field '" + target.getClass().getName() + "' inject fail");
        }
    }

}
