package com.xiaojinzi.component.impl

import com.xiaojinzi.component.router.IComponentHostRouterDegrade
import com.xiaojinzi.component.bean.RouterDegradeBean
import androidx.annotation.CallSuper
import java.util.ArrayList

abstract class ModuleRouterDegradeImpl : IComponentHostRouterDegrade {

    /**
     * 降级处理的类
     */
    @JvmField
    protected val routerDegradeBeanList: List<RouterDegradeBean> = ArrayList()

    /**
     * 是否初始化了map,懒加载
     */
    private var hasInitList = false

    @CallSuper
    protected open fun initList() {
        hasInitList = true
    }

    override fun listRouterDegrade(): List<RouterDegradeBean> {
        if (!hasInitList) {
            initList()
        }
        return ArrayList(routerDegradeBeanList)
    }

}