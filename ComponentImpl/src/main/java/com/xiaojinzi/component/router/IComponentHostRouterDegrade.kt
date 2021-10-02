package com.xiaojinzi.component.router

import com.xiaojinzi.component.bean.RouterDegradeBean
import com.xiaojinzi.component.support.IHost

/**
 * 模块的路由的降级处理
 *
 *
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
interface IComponentHostRouterDegrade : IHost {

    /**
     * 获取这个模块的降级处理
     */
    fun listRouterDegrade(): List<RouterDegradeBean>

}