package com.xiaojinzi.component.router

import com.xiaojinzi.component.impl.RouterDegrade
import java.lang.Exception

/**
 * 路由的降级处理
 *
 *
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
interface IComponentRouterDegrade {

    /**
     * 获取所有的降级处理, 数据需要排过序的
     *
     * @throws Exception
     */
    @get:Throws(Exception::class)
    val globalRouterDegradeList: List<RouterDegrade>

}