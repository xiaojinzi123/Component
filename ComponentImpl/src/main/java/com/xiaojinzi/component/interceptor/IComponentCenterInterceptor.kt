package com.xiaojinzi.component.interceptor

import androidx.annotation.UiThread
import com.xiaojinzi.component.impl.RouterInterceptor
import com.xiaojinzi.component.support.IComponentCenter

/**
 * 拦截器管理中心的接口
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
interface IComponentCenterInterceptor : IComponentInterceptor, IComponentCenter<IComponentHostInterceptor> {

    /**
     * 获取全局拦截器列表
     *
     * @return 全局的拦截器列表
     */
    @get:UiThread
    val globalInterceptorList: List<RouterInterceptor>

}