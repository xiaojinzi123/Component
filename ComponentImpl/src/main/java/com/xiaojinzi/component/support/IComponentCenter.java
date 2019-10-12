package com.xiaojinzi.component.support;

import androidx.annotation.NonNull;

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
    void register(T t);

    /**
     * 通过host注册
     *
     * @param host
     */
    void register(@NonNull String host);

    /**
     * 反注册
     *
     * @param t
     */
    void unregister(T t);

    /**
     * 通过 host 反注册
     *
     * @param host
     */
    void unregister(@NonNull String host);

}
