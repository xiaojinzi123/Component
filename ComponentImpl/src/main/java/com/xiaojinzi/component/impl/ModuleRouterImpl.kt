package com.xiaojinzi.component.impl

import com.xiaojinzi.component.router.IComponentHostRouter
import com.xiaojinzi.component.bean.RouterBean
import androidx.annotation.CallSuper
import java.util.HashMap

/**
 * 如果名称更改了,请配置到 [ComponentUtil.IMPL_OUTPUT_PKG] 和 [ComponentUtil.UIROUTER_IMPL_CLASS_NAME] 上
 * 因为这个类是生成的子路由需要继承的类,所以这个类的包的名字的更改或者类名更改都会引起源码或者配置常量的更改
 *
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
internal abstract class ModuleRouterImpl : IComponentHostRouter {

    /**
     * 正则匹配的集合
     */
    protected val regExRouterBeanMap: Map<String, RouterBean> = HashMap()

    /**
     * router://component/test
     * 保存映射关系的map集合
     */
    protected val routerBeanMap: Map<String, RouterBean> = HashMap()

    /**
     * 是否初始化了map,懒加载
     */
    private var hasInitMap = false

    /**
     * 必须调用父类的方法
     */
    @CallSuper
    protected open fun initMap() {
        hasInitMap = true
    }

    override fun getRegExRouterMap(): Map<String, RouterBean> {
        if (!hasInitMap) {
            initMap()
        }
        return if (regExRouterBeanMap.isEmpty()) {
            emptyMap()
        } else HashMap(regExRouterBeanMap)
    }

    /**
     * 获取路由表
     */
    override fun getRouterMap(): Map<String, RouterBean> {
        if (!hasInitMap) {
            initMap()
        }
        return if (routerBeanMap.isEmpty()) {
            emptyMap()
        } else HashMap(routerBeanMap)
    }

}