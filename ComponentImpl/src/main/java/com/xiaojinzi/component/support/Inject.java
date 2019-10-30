package com.xiaojinzi.component.support;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.xiaojinzi.component.anno.support.CheckClassName;

/**
 * 每一个生成的类都应该实现这个接口
 */
@CheckClassName
public interface Inject<T> {

    /**
     * 注入控件
     *
     * @param target 目标界面
     */
    void inject(@NonNull T target);

    /**
     * 注入属性值
     *
     * @param target 目标界面
     * @param bundle 数据源的 bundle
     */
    void inject(@NonNull T target, @NonNull Bundle bundle);

}
