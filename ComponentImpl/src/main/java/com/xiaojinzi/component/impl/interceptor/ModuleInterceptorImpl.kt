package com.xiaojinzi.component.impl.interceptor

import android.app.Application
import com.xiaojinzi.component.interceptor.IComponentHostInterceptor
import com.xiaojinzi.component.impl.RouterInterceptor
import androidx.annotation.UiThread
import androidx.annotation.CallSuper
import com.xiaojinzi.component.support.RouterInterceptorCache
import java.util.HashMap

/**
 * 拦截器的代码生成类的基本实现
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
internal abstract class ModuleInterceptorImpl : IComponentHostInterceptor {

    private var isInitMap = false

    protected val realInterceptorMap: Map<String, Class<out RouterInterceptor>> = mutableMapOf()

    override val interceptorMap: Map<String, Class<out RouterInterceptor>>
        get() {
            if (!isInitMap) {
                initInterceptorMap()
            }
            return HashMap(realInterceptorMap)
        }

    override val interceptorNames: Set<String>
        get() {
            if (!isInitMap) {
                initInterceptorMap()
            }
            return realInterceptorMap.keys
        }

    /**
     * 初始化拦截器的集合
     */
    @CallSuper
    @UiThread
    protected open fun initInterceptorMap() {
        isInitMap = true
    }

    /**
     * 用作销毁一些缓存
     *
     * @param app [Application]
     */
    override fun onCreate(app: Application) {
        // empty
    }

    /**
     * 用作销毁一些缓存
     */
    override fun onDestroy() {
        // empty
    }

    @UiThread
    override fun globalInterceptorList(): List<InterceptorBean> {
        return emptyList()
    }

    @UiThread
    override fun getByName(name: String): RouterInterceptor? {
        if (!isInitMap) {
            initInterceptorMap()
        }
        val interceptorClass = interceptorMap[name] ?: return null
        return RouterInterceptorCache.getInterceptorByClass(interceptorClass)
    }

}