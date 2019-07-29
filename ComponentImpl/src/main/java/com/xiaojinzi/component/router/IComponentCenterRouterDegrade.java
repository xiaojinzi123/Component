package com.xiaojinzi.component.router;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RouterRequest;

import java.util.List;

/**
 * 模块的UI路由的接口
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentCenterRouterDegrade extends IComponentRouterDegrade {

    /**
     * 注册每一个模块的降级处理
     *
     * @param routerDegrade
     */
    void register(@Nullable IComponentHostRouterDegrade routerDegrade);

    /**
     * 通过host注册
     *
     * @param host
     */
    void register(@NonNull String host);

    /**
     * 反注册模块的降级处理
     *
     * @param routerDegrade
     */
    void unregister(@Nullable IComponentHostRouterDegrade routerDegrade);

    /**
     * 通过 host 反注册
     *
     * @param host
     */
    void unregister(@NonNull String host);

    /**
     * 根据 {@link RouterRequest} 获取对应的页面拦截器
     *
     * @param request
     * @return
     * @throws Exception
     */
    @NonNull
    List<RouterInterceptor> listDegradeInterceptors(@NonNull RouterRequest request) throws Exception;

}
