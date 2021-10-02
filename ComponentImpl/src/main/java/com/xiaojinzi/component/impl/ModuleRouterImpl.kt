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
    override val regExRouterMap: Map<String, RouterBean> = HashMap()
        get() {
            if (!hasInitMap) {
                initMap()
            }
            return if (field.isEmpty()) {
                emptyMap()
            } else {
                HashMap(field)
            }
        }

    /**
     * router://component/test
     * 保存映射关系的map集合
     */
    override val routerMap: Map<String, RouterBean> = HashMap()
        get() {
            if (!hasInitMap) {
                initMap()
            }
            return if (field.isEmpty()) {
                emptyMap()
            } else {
                HashMap(field)
            }
        }

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

}