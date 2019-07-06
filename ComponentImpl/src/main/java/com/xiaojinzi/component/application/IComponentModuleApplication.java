package com.xiaojinzi.component.application;

import androidx.annotation.NonNull;

/**
 * Module 管理中心的接口,用于管理所有 Module
 */
public interface IComponentModuleApplication {

    /**
     * 注册每一个模块
     *
     * @param moduleApp
     */
    void register(@NonNull IComponentHostApplication moduleApp);

    /**
     * 通过host注册
     *
     * @param host
     */
    void register(@NonNull String host);

    /**
     * 反注册模块
     *
     * @param moduleApp
     */
    void unregister(@NonNull IComponentHostApplication moduleApp);

    /**
     * 通过 host 反注册
     *
     * @param host
     */
    void unregister(@NonNull String host);

}
