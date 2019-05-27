package com.xiaojinzi.component.router;

import androidx.annotation.NonNull;

/**
 * 模块的UI路由的接口
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentCenterRouter extends IComponentRouter {

    /**
     * 注册每一个模块的注解路由器
     *
     * @param router
     */
    void register(IComponentHostRouter router);

    /**
     * 通过host注册
     *
     * @param host
     */
    void register(@NonNull String host);

    /**
     * 反注册模块的注解路由器
     *
     * @param router
     */
    void unregister(IComponentHostRouter router);

    /**
     * 通过 host 反注册
     *
     * @param host
     */
    void unregister(@NonNull String host);

}
