package com.xiaojinzi.component.impl.fragment

import com.xiaojinzi.component.Component.getApplication
import com.xiaojinzi.component.Component.requiredConfig
import com.xiaojinzi.component.ComponentUtil
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.fragment.IComponentCenterFragment
import com.xiaojinzi.component.fragment.IComponentHostFragment
import com.xiaojinzi.component.support.ASMUtil
import com.xiaojinzi.component.support.Utils
import java.util.*

/**
 * 模块 Fragment 注册和卸载的总管
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
object FragmentCenter: IComponentCenterFragment {

    private val moduleFragmentMap: MutableMap<String, IComponentHostFragment> = HashMap()

    override fun register(hostFragment: IComponentHostFragment) {
        Utils.checkNullPointer(hostFragment)
        if (!moduleFragmentMap.containsKey(hostFragment.host)) {
            moduleFragmentMap[hostFragment.host] = hostFragment
            hostFragment.onCreate(getApplication())
        }
    }

    override fun register(host: String) {
        Utils.checkStringNullPointer(host, "host")
        if (!moduleFragmentMap.containsKey(host)) {
            val moduleService = findModuleService(host)
            moduleService?.let { register(it) }
        }
    }

    override fun unregister(hostFragment: IComponentHostFragment) {
        Utils.checkNullPointer(hostFragment)
        moduleFragmentMap.remove(hostFragment.host)
        hostFragment.onDestroy()
    }

    override fun unregister(host: String) {
        Utils.checkStringNullPointer(host, "host")
        val moduleService = moduleFragmentMap[host]
        moduleService?.let { unregister(it) }
    }

    fun findModuleService(host: String?): IComponentHostFragment? {
        try {
            return if (requiredConfig().isOptimizeInit) {
                ASMUtil.findModuleFragmentAsmImpl(
                        ComponentUtil.transformHostForClass(host)
                )
            } else {
                var clazz: Class<out IComponentHostFragment?>? = null
                val className = ComponentUtil.genHostFragmentClassName(host)
                clazz = Class.forName(className) as Class<out IComponentHostFragment>
                clazz.newInstance()
            }
        } catch (ignore: Exception) {
            // ignore
        }
        return null
    }

    fun check() {
        val set: MutableSet<String> = HashSet()
        for ((_, childRouter) in moduleFragmentMap) {
            if (childRouter?.fragmentNameSet == null) {
                continue
            }
            val childRouterSet = childRouter.fragmentNameSet
            for (key in childRouterSet) {
                check(!set.contains(key)) { "the name of Fragment is exist：'$key'" }
                set.add(key)
            }
        }
    }

}