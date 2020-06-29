package com.xiaojinzi.component.support;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

/**
 * 模块管理中心注册的类
 */
public interface IComponentCenter<T> {

    /**
     * 注册
     *
     * @param t 注册的目标
     */
    @MainThread
    void register(T t);

    /**
     * 通过host注册
     *
     * @param host 需要注册的 host
     */
    @MainThread
    void register(@NonNull String host);

    /**
     * 反注册
     *
     * @param t 反注册的目标
     */
    @MainThread
    void unregister(T t);

    /**
     * 通过 host 反注册
     *
     * @param host 反注册的 host
     */
    @MainThread
    void unregister(@NonNull String host);

}
