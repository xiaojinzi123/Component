package com.xiaojinzi.component.support

import com.xiaojinzi.component.anno.support.CheckClassNameAnno

/**
 * 单例服务,这是注册服务默认的形式
 */
@CheckClassNameAnno
abstract class SingletonCallable<T> : Callable<T> {

    @Volatile
    private var instance: T? = null

    val isInit: Boolean
        get() = instance != null

    /**
     * 获取真正的对象
     */
    protected abstract val raw: T

    override fun get(): T {
        if (null == instance) {
            synchronized(this) {
                if (null == instance) {
                    instance = raw
                }
            }
        }
        return instance!!
    }

    @Synchronized
    fun destroy() {
        instance = null
    }

}