package com.xiaojinzi.component.impl.interceptor

import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import com.xiaojinzi.component.Component.requiredConfig
import com.xiaojinzi.component.ComponentUtil
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.error.InterceptorNameExistException
import com.xiaojinzi.component.impl.RouterInterceptor
import com.xiaojinzi.component.interceptor.IComponentCenterInterceptor
import com.xiaojinzi.component.interceptor.IComponentHostInterceptor
import com.xiaojinzi.component.support.ASMUtil
import com.xiaojinzi.component.support.RouterInterceptorCache
import com.xiaojinzi.component.support.Utils
import java.util.*

/**
 * 中央拦截器
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
@CheckClassNameAnno
object InterceptorCenter: IComponentCenterInterceptor {

    /**
     * 子拦截器对象管理 map
     */
    private val moduleInterceptorMap: MutableMap<String, IComponentHostInterceptor> = HashMap()

    /**
     * 公共的拦截器列表
     */
    private val mGlobalInterceptorList: MutableList<RouterInterceptor> = ArrayList()

    /**
     * 每个业务组件的拦截器 name --> Class 映射关系的总的集合
     * 这种拦截器不是全局拦截器,是随时随地使用的拦截器,见 [com.xiaojinzi.component.impl.Navigator.interceptorNames]
     */
    private val mInterceptorMap: MutableMap<String, Class<out RouterInterceptor>> = HashMap()

    /**
     * 是否公共的拦截器列表发生变化
     */
    private var isInterceptorListHaveChange = false

    /**
     * 获取全局拦截器
     */
    @get:UiThread
    override val globalInterceptorList: List<RouterInterceptor>
        get() {
            if (isInterceptorListHaveChange) {
                loadAllGlobalInterceptor()
                isInterceptorListHaveChange = false
            }
            return mGlobalInterceptorList
        }

    override fun register(hostInterceptor: IComponentHostInterceptor) {
        Utils.checkNullPointer(hostInterceptor)
        if (!moduleInterceptorMap.containsKey(hostInterceptor.host)) {
            isInterceptorListHaveChange = true
            moduleInterceptorMap[hostInterceptor.host] = hostInterceptor
            // 子拦截器列表
            val childInterceptorMap = hostInterceptor.interceptorMap
            mInterceptorMap.putAll(childInterceptorMap)
        }
    }

    override fun register(host: String) {
        Utils.checkStringNullPointer(host, "host")
        if (!moduleInterceptorMap.containsKey(host)) {
            val moduleInterceptor = findModuleInterceptor(host)
            moduleInterceptor?.let { register(it) }
        }
    }

    override fun unregister(hostInterceptor: IComponentHostInterceptor) {
        Utils.checkNullPointer(hostInterceptor)
        moduleInterceptorMap.remove(hostInterceptor.host)
        isInterceptorListHaveChange = true
        // 子拦截器列表
        val childInterceptorMap: Map<String, Class<out RouterInterceptor?>?> = hostInterceptor.interceptorMap
        for ((key, value) in childInterceptorMap) {
            mInterceptorMap.remove(key)
            RouterInterceptorCache.removeCache(value!!)
        }
    }

    override fun unregister(host: String) {
        Utils.checkStringNullPointer(host, "host")
        val moduleInterceptor = moduleInterceptorMap[host]
        moduleInterceptor?.let { unregister(it) }
    }

    /**
     * 按顺序弄好所有全局拦截器
     */
    @UiThread
    private fun loadAllGlobalInterceptor() {
        mGlobalInterceptorList.clear()
        val totalList: MutableList<InterceptorBean> = ArrayList()
        // 加载各个子拦截器对象中的拦截器列表
        for ((_, value) in moduleInterceptorMap) {
            val list = value.globalInterceptorList()
            totalList.addAll(list)
        }
        // 排序所有的拦截器对象,按照优先级排序
        totalList.sortByDescending { it.priority }
        for ((interceptor) in totalList) {
            mGlobalInterceptorList.add(interceptor)
        }
    }

    @AnyThread
    fun findModuleInterceptor(host: String): IComponentHostInterceptor? {
        try {
            return if (requiredConfig().isOptimizeInit) {
                ASMUtil.findModuleInterceptorAsmImpl(
                        ComponentUtil.transformHostForClass(host)
                )
            } else {
                var clazz: Class<out IComponentHostInterceptor?>? = null
                val className = ComponentUtil.genHostInterceptorClassName(host)
                clazz = Class.forName(className) as Class<out IComponentHostInterceptor?>
                clazz.newInstance()
            }
        } catch (ignore: Exception) {
            // ignore
        }
        return null
    }

    @UiThread
    override fun getByName(interceptorName: String): RouterInterceptor? {
        if (interceptorName == null) {
            return null
        }
        // 先到缓存中找
        var result: RouterInterceptor? = null
        // 拿到拦截器的 Class 对象
        val interceptorClass = mInterceptorMap[interceptorName]
        result = if (interceptorClass == null) {
            null
        } else {
            RouterInterceptorCache.getInterceptorByClass(interceptorClass)
        }
        return result
    }

    /**
     * 做拦截器的名称是否重复的工作
     */
    @UiThread
    fun check() {
        Utils.checkMainThread()
        val set: MutableSet<String> = HashSet()
        for ((_, value) in moduleInterceptorMap) {
            val childInterceptor = value ?: continue
            val childInterceptorNames = childInterceptor.interceptorNames
            if (childInterceptorNames.isEmpty()) {
                continue
            }
            for (interceptorName in childInterceptorNames) {
                if (set.contains(interceptorName)) {
                    throw InterceptorNameExistException("the interceptor's name is exist：$interceptorName")
                }
                set.add(interceptorName)
            }
        }
    }

}