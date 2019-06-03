package com.xiaojinzi.component.impl;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.ComponentConstants;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.Inject;
import com.xiaojinzi.component.support.Utils;

/**
 * 项目代表类
 */
public class Component {

    /**
     * 找到实现类,执行注入
     *
     * @param target
     */
    public static void inject(@NonNull Object target) {
        Utils.checkNullPointer(target, "target");
        String injectClassName = target.getClass().getName() + ComponentConstants.INJECT_SUFFIX;
        try {
            Class<?> targetInjectClass = Class.forName(injectClassName);
            Inject inject = (Inject) targetInjectClass.newInstance();
            inject.inject(target);
        } catch (Exception ignore) {
            LogUtil.log(target.getClass().getName(), "field inject fail");
        }
    }

}
