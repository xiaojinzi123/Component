package com.xiaojinzi.component.application;

import android.support.annotation.NonNull;


public interface IComponentModuleApplication {

    /**
     * 注册每一个模块
     *
     * @param moduleApp
     */
    void register(IComponentHostApplication moduleApp);

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
    void unregister(IComponentHostApplication moduleApp);

    /**
     * 通过 host 反注册
     *
     * @param host
     */
    void unregister(@NonNull String host);

}
