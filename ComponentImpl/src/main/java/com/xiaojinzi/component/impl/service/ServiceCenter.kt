package com.xiaojinzi.component.impl.service

import com.xiaojinzi.component.Component.getApplication
import com.xiaojinzi.component.Component.requiredConfig
import com.xiaojinzi.component.ComponentUtil
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.service.IComponentCenterService
import com.xiaojinzi.component.service.IComponentHostService
import com.xiaojinzi.component.support.ASMUtil
import com.xiaojinzi.component.support.Utils
import java.util.*

/**
 * 模块服务注册和卸载的总管
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
object ServiceCenter: IComponentCenterService {

    private val moduleServiceMap: MutableMap<String, IComponentHostService> = HashMap()

    override fun register(service: IComponentHostService) {
        Utils.checkNullPointer(service)
        if (!moduleServiceMap.containsKey(service.host)) {
            moduleServiceMap[service.host] = service
            service.onCreate(getApplication())
        }
    }

    override fun register(host: String) {
        Utils.checkStringNullPointer(host, "host")
        if (!moduleServiceMap.containsKey(host)) {
            val moduleService = findModuleService(host)
            moduleService?.let { register(it) }
        }
    }

    override fun unregister(moduleService: IComponentHostService) {
        Utils.checkNullPointer(moduleService)
        moduleServiceMap.remove(moduleService.host)
        moduleService.onDestroy()
    }

    override fun unregister(host: String) {
        Utils.checkStringNullPointer(host, "host")
        val moduleService = moduleServiceMap[host]
        moduleService?.let { unregister(it) }
    }

    private fun findModuleService(host: String): IComponentHostService? {
        try {
            return if (requiredConfig().isOptimizeInit) {
                ASMUtil.findModuleServiceAsmImpl(
                        ComponentUtil.transformHostForClass(host)
                )
            } else {
                var clazz: Class<out IComponentHostService?>? = null
                val className = ComponentUtil.genHostServiceClassName(host)
                clazz = Class.forName(className) as Class<out IComponentHostService>
                clazz.newInstance()
            }
        } catch (ignore: Exception) {
            // ignore
        }
        return null
    }

}