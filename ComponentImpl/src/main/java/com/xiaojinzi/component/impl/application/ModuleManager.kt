package com.xiaojinzi.component.impl.application

import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Component.check
import com.xiaojinzi.component.Component.getApplication
import com.xiaojinzi.component.Component.requiredConfig
import com.xiaojinzi.component.ComponentUtil
import com.xiaojinzi.component.application.IComponentCenterApplication
import com.xiaojinzi.component.application.IComponentHostApplication
import com.xiaojinzi.component.cache.ClassCache
import com.xiaojinzi.component.impl.RouterCenter
import com.xiaojinzi.component.impl.RouterDegradeCenter
import com.xiaojinzi.component.impl.fragment.FragmentCenter
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter
import com.xiaojinzi.component.impl.service.ServiceCenter
import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.component.support.ASMUtil
import com.xiaojinzi.component.support.LogUtil
import com.xiaojinzi.component.support.Utils
import java.util.*

/**
 * 这个类必须放在 [ComponentUtil.IMPL_OUTPUT_PKG] 包下面
 * 这是是管理每一个模块之前联系的管理类,加载模块的功能也是这个类负责的
 *
 * @author xiaojinzi
 */
object ModuleManager : IComponentCenterApplication {

    private val moduleApplicationMap: MutableMap<String, IComponentHostApplication> = HashMap()

    fun findModuleApplication(host: String): IComponentHostApplication? {
        var result: IComponentHostApplication? = null
        if (requiredConfig().isOptimizeInit) {
            LogUtil.log("\"$host\" will try to load by bytecode")
            result = ASMUtil.findModuleApplicationAsmImpl(
                    ComponentUtil.transformHostForClass(host)
            )
        } else {
            LogUtil.log("\"$host\" will try to load by reflection")
            if (result == null) {
                try {
                    // 先找正常的
                    val clazz = Class.forName(ComponentUtil.genHostModuleApplicationClassName(host))
                    result = clazz.newInstance() as IComponentHostApplication
                } catch (ignore: Exception) {
                    // ignore
                }
            }
            if (result == null) {
                try {
                    // 找默认的
                    val clazz = Class.forName(ComponentUtil.genDefaultHostModuleApplicationClassName(host))
                    result = clazz.newInstance() as IComponentHostApplication
                } catch (ignore: Exception) {
                    // ignore
                }
            }
        }
        return result
    }

    @UiThread
    override fun register(moduleApp: IComponentHostApplication) {
        Utils.checkNullPointer(moduleApp)
        if (moduleApplicationMap.containsKey(moduleApp.host)) {
            LogUtil.loge("The module \"" + moduleApp.host + "\" is already registered")
        } else {
            // 标记已经注册
            moduleApplicationMap[moduleApp.host] = moduleApp
            // 模块的 Application 的 onCreate 执行
            moduleApp.onCreate(getApplication())
            // 服务发现的注册. 这个不能异步, 因为有可能下一行就被用到了
            ServiceCenter.register(moduleApp.host)
            // 路由的部分的注册, 可选的异步还是同步
            val r = Runnable {
                RouterCenter.register(moduleApp.host)
                InterceptorCenter.register(moduleApp.host)
                RouterDegradeCenter.register(moduleApp.host)
                FragmentCenter.register(moduleApp.host)
                notifyModuleChanged()
            }
            // 路由是否异步初始化
            if (requiredConfig().isInitRouterAsync) {
                Utils.postActionToWorkThread(r)
            } else {
                r.run()
            }
        }
    }

    override fun register(host: String) {
        Utils.checkNullPointer(host, "host")
        if (moduleApplicationMap.containsKey(host)) {
            LogUtil.loge("the host '$host' is already load")
            return
        } else {
            val moduleApplication = findModuleApplication(host)
            if (moduleApplication == null) {
                LogUtil.log("模块 '$host' 加载失败, 请根据链接中的内容自行排查! ${Component.COMMON_ERROR_ISSUE}")
            } else {
                register(moduleApplication)
            }
        }
    }

    /**
     * 自动注册, 需要开启 [com.xiaojinzi.component.Config.Builder.optimizeInit]
     * 表示使用 Gradle 插件优化初始化
     */
    fun autoRegister() {
        if (!requiredConfig().isOptimizeInit) {
            LogUtil.logw("you can't use this method to register module. Because you not turn on 'optimizeInit' by calling method 'Config.Builder.optimizeInit(true)' when you init")
        }
        val moduleNames = ASMUtil.getModuleNames()
        if (moduleNames.isNotEmpty()) {
            registerArr(*moduleNames.toTypedArray())
        }
    }

    /**
     * 注册业务模块, 可以传多个名称
     *
     * @param hosts host 的名称数组
     */
    fun registerArr(vararg hosts: String) {
        val appList: MutableList<IComponentHostApplication> = ArrayList(hosts.size)
        for (host in hosts) {
            val moduleApplication = findModuleApplication(host!!)
            if (moduleApplication == null) {
                LogUtil.log("模块 '$host' 加载失败, 请根据链接中的内容自行排查! ${Component.COMMON_ERROR_ISSUE}")
            } else {
                appList.add(moduleApplication)
            }
        }
        // 处理优先级, 数值大的先加载
        appList.sortWith { o1, o2 -> o2.priority - o1.priority }
        for (moduleApplication in appList) {
            register(moduleApplication)
        }
    }

    @UiThread
    override fun unregister(moduleApp: IComponentHostApplication) {
        moduleApplicationMap.remove(moduleApp.host)
        moduleApp.onDestroy()
        ServiceCenter.unregister(moduleApp.host)
        Utils.postActionToWorkThread {
            RouterCenter.unregister(moduleApp.host)
            InterceptorCenter.unregister(moduleApp.host)
            RouterDegradeCenter.unregister(moduleApp.host)
            FragmentCenter.unregister(moduleApp.host)
            // 清空缓存
            ClassCache.clear()
            notifyModuleChanged()
        }
    }

    override fun unregister(host: String) {
        Utils.checkNullPointer(host, "host")
        val moduleApp = moduleApplicationMap[host]
        if (moduleApp == null) {
            LogUtil.log("模块 '$host' 卸载失败")
        } else {
            unregister(moduleApp)
        }
    }

    fun unregisterAll() {
        // 创建一个 HashSet 是为了能循环的时候删除集合中的元素
        for (host in HashSet(moduleApplicationMap.keys)) {
            unregister(host)
        }
    }

    @AnyThread
    private fun notifyModuleChanged() {
        // 当前的值
        val compareValue = Utils.COUNTER.incrementAndGet()
        Utils.postDelayActionToMainThread({
            // 说明没有改变过
            if (compareValue == Utils.COUNTER.get()) {
                // LogUtil.loge("通知 " + compareValue);
                doNotifyModuleChanged()
            } else {
                // LogUtil.loge("放弃通知 " + compareValue);
            }
        }, requiredConfig().notifyModuleChangedDelayTime)
    }

    @UiThread
    private fun doNotifyModuleChanged() {
        for (hostApplication in moduleApplicationMap.values) {
            hostApplication.onModuleChanged(getApplication())
        }
        // 内部有 debug 判断
        check()
        // 触发自动初始化
        Utils.postActionToWorkThread { ServiceManager.autoInitService() }
    }

}