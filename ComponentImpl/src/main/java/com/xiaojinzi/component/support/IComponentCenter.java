package com.xiaojinzi.component.support;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

/**
 * 模块管理中心注册的类
 *
 * @param <T>
 */
public interface IComponentCenter<T> {

    /**
     * 注册
     *
     * @param t
     */
    @MainThread
    void register(T t);

    /**
     * 通过host注册
     *
     * @param host
     */
    @MainThread
    void register(@NonNull String host);

    /**
     * 反注册
     *
     * @param t
     */
    @MainThread
    void unregister(T t);

    /**
     * 通过 host 反注册
     *
     * @param host
     */
    @MainThread
    void unregister(@NonNull String host);

}
