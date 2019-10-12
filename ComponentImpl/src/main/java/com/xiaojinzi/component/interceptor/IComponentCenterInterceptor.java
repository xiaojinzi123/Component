package com.xiaojinzi.component.interceptor;

import androidx.annotation.NonNull;

import com.xiaojinzi.component.support.IComponentCenter;
import com.xiaojinzi.component.impl.RouterInterceptor;

import java.util.List;

/**
 * 拦截器管理中心的接口
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentCenterInterceptor extends IComponentInterceptor, IComponentCenter<IComponentHostInterceptor> {

    /**
     * 获取全局拦截器列表
     *
     * @return 全局的拦截器列表
     */
    @NonNull
    List<RouterInterceptor> getGlobalInterceptorList();

}
