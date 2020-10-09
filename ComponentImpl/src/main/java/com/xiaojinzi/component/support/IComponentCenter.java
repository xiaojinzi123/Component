package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

/**
 * 模块管理中心注册的类
 */
public interface IComponentCenter<T> {

    /**
     * 注册
     *
     * @param t 注册的目标
     */
    @UiThread
    void register(@NonNull T t);

    /**
     * 通过host注册
     *
     * @param host 需要注册的 host
     */
    @UiThread
    void register(@NonNull String host);

    /**
     * 反注册
     *
     * @param t 反注册的目标
     */
    @UiThread
    void unregister(@NonNull T t);

    /**
     * 通过 host 反注册
     *
     * @param host 反注册的 host
     */
    @UiThread
    void unregister(@NonNull String host);

}
