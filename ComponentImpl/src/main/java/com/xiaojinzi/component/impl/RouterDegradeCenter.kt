package com.xiaojinzi.component.impl

import androidx.annotation.Keep
import com.xiaojinzi.component.Component.requiredConfig
import com.xiaojinzi.component.ComponentUtil
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.router.IComponentCenterRouterDegrade
import com.xiaojinzi.component.router.IComponentHostRouterDegrade
import com.xiaojinzi.component.support.ASMUtil
import com.xiaojinzi.component.support.RouterDegradeCache
import com.xiaojinzi.component.support.Utils
import java.util.*

@Keep
class RouterDegradeItem(
        /**
         * 优先级
         */
        var priority: Int,
        var routerDegrade: RouterDegrade
)

@CheckClassNameAnno
object RouterDegradeCenter : IComponentCenterRouterDegrade {

    /**
     * 子拦截器对象管理 map
     */
    private val moduleRouterDegradeMap: MutableMap<String, IComponentHostRouterDegrade> = mutableMapOf()

    /**
     * 全局的降级处理, 数据一定是排序过的
     */
    private val mGlobalRouterDegradeList: MutableList<RouterDegradeItem> = mutableListOf()

    /**
     * 是否降级处理列表发生变化
     */
    private var isRouterDegradeListHaveChange = false

    override val globalRouterDegradeList: List<RouterDegrade>
        get() {
            if (isRouterDegradeListHaveChange) {
                loadAllGlobalRouterDegrade()
                isRouterDegradeListHaveChange = false
            }
            val result: MutableList<RouterDegrade> = ArrayList()
            for (item in mGlobalRouterDegradeList) {
                result.add(item.routerDegrade)
            }
            return result
        }

    /**
     * 按顺序弄好所有全局拦截器
     */
    private fun loadAllGlobalRouterDegrade() {
        mGlobalRouterDegradeList.clear()
        moduleRouterDegradeMap
                .asSequence()
                .flatMap { it.value.listRouterDegrade() }
                .map {
                    RouterDegradeItem(
                            it.priority,
                            RouterDegradeCache.getRouterDegradeByClass(it.targetClass!!)!!
                    )
                }
                .sortedByDescending { it.priority }
                .forEach {
                    mGlobalRouterDegradeList.add(it)
                }
    }

    override fun register(routerDegrade: IComponentHostRouterDegrade) {
        Utils.checkNullPointer(routerDegrade)
        isRouterDegradeListHaveChange = true
        moduleRouterDegradeMap[routerDegrade.host] = routerDegrade
    }

    override fun register(host: String) {
        Utils.checkStringNullPointer(host, "host")
        if (!moduleRouterDegradeMap.containsKey(host)) {
            val moduleRouterDegrade = findModuleRouterDegrade(host)
            moduleRouterDegrade?.let { register(it) }
        }
    }

    override fun unregister(routerDegrade: IComponentHostRouterDegrade) {
        moduleRouterDegradeMap.remove(routerDegrade.host)
        isRouterDegradeListHaveChange = true
    }

    override fun unregister(host: String) {
        Utils.checkStringNullPointer(host, "host")
        val moduleRouterDegrade = moduleRouterDegradeMap[host]
        moduleRouterDegrade?.let { unregister(it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun findModuleRouterDegrade(host: String): IComponentHostRouterDegrade? {
        try {
            return if (requiredConfig().isOptimizeInit) {
                ASMUtil.findModuleRouterDegradeAsmImpl(
                        ComponentUtil.transformHostForClass(host)
                )
            } else {
                var clazz: Class<out IComponentHostRouterDegrade?>? = null
                val className = ComponentUtil.genHostRouterDegradeClassName(host)
                clazz = Class.forName(className) as Class<out IComponentHostRouterDegrade>
                clazz.newInstance()
            }
        } catch (ignore: Exception) {
            // ignore
        }
        return null
    }


}