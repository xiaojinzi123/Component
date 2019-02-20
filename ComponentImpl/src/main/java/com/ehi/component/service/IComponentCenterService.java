package com.ehi.component.service;

import android.support.annotation.NonNull;

/**
 * 模块的UI服务接口
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentCenterService {

    /**
     * 注册每一个模块的服务
     *
     * @param service
     */
    void register(@NonNull IComponentHostService service);

    /**
     * 通过host注册
     *
     * @param host
     */
    void register(@NonNull String host);

    /**
     * 反注册模块的服务
     *
     * @param service
     */
    void unregister(@NonNull IComponentHostService service);

    /**
     * 通过 host 反注册
     *
     * @param host
     */
    void unregister(@NonNull String host);

}
