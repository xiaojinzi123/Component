package com.xiaojinzi.component.interceptor;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.support.IComponentCenter;

import java.util.List;

/**
 * 拦截器管理中心的接口
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
public interface IComponentCenterInterceptor extends IComponentInterceptor, IComponentCenter<IComponentHostInterceptor> {

    /**
     * 获取全局拦截器列表
     *
     * @return 全局的拦截器列表
     */
    @NonNull
    @UiThread
    List<RouterInterceptor> getGlobalInterceptorList();

}
