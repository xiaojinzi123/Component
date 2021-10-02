package com.xiaojinzi.component.router

import com.xiaojinzi.component.bean.RouterBean
import com.xiaojinzi.component.support.IHost

/**
 * 组件之间实现跳转的接口
 *
 *
 * time   : 2018/07/26
 * @author : xiaojinzi
 */
interface IComponentHostRouter : IHost {

    /**
     * 获取正则匹配的路由表
     */
    val regExRouterMap: Map<String, RouterBean>

    /**
     * 获取路由表
     */
    val routerMap: Map<String, RouterBean>

}