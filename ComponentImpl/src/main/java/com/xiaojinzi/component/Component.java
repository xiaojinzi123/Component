package com.xiaojinzi.component;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.xiaojinzi.component.impl.RouterCenter;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component.impl.fragment.FragmentCenter;
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter;
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

    public static final String GITHUB_URL = "https://github.com/xiaojinzi123/Component";
    public static final String DOC_URL = "https://github.com/xiaojinzi123/Component/wiki";
    public static final String COMMON_ERROR_ISSUE = "https://github.com/xiaojinzi123/Component/issues/21";
    public static final String ROUTER_UES_NOTE = "https://github.com/xiaojinzi123/Component/wiki/%E4%B8%BB%E9%A1%B5#%E7%89%B9%E5%88%AB%E6%B3%A8%E6%84%8F";

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
    @UiThread
    public static void init(boolean isDebug, @NonNull Config config) {
        // 做必要的检查
        if (isInit) {
            throw new RuntimeException("you have init Component already!");
        }
        Utils.checkMainThread();
        Utils.checkNullPointer(config, "config");
        Component.isDebug = isDebug;
        mConfig = config;
        if (isDebug) {
            printComponent();
        }
        // 注册
        mConfig.getApplication().registerActivityLifecycleCallbacks(new ComponentLifecycleCallback());
        if (mConfig.isOptimizeInit() && mConfig.isAutoRegisterModule()) {
            ModuleManager.getInstance().autoRegister();
        }
        isInit = true;
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
                .append("Github 地址：" + GITHUB_URL)
                .append("\n文档地址：" + DOC_URL)
                .append("\n错误排查指南：" + COMMON_ERROR_ISSUE)
                .append("\n ");

        LogUtil.logw(sb.toString());
    }

    @NonNull
    @AnyThread
    public static Config getConfig() {
        checkInit();
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

    @UiThread
    public static void inject(@NonNull Object target) {
        inject(target, null, true, true);
    }

    @UiThread
    public static void injectAttrValueFromIntent(@NonNull Object target, @Nullable Intent intent) {
        injectAttrValueFromBundle(target, intent == null ? null : intent.getExtras());
    }

    @UiThread
    public static void injectAttrValueFromBundle(@NonNull Object target, @Nullable Bundle bundle) {
        inject(target, bundle, true, false);
    }

    @UiThread
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
    @UiThread
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
            LogUtil.log("class '" + target.getClass().getName() + "' inject fail");
        }
    }

    /**
     * 使用者应该在开发阶段调用这个函数来检查以下的问题：
     * 1.路由表在不同的子路由表中是否有重复
     * 2.服务在不同模块中的声明是否也有重复的名称
     */
    public static void check() {
        if (isDebug()) {
            RouterCenter.getInstance().check();
            InterceptorCenter.getInstance().check();
            FragmentCenter.getInstance().check();
        }
    }

}
