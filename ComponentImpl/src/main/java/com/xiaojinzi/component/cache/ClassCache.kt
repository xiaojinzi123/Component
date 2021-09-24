package com.xiaojinzi.component.cache

import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.cache.CacheType.Companion.CLASS_CACHE

/**
 * Class 的缓存的工具类
 */
@CheckClassNameAnno
object ClassCache {

    private val classCache = DefaultCacheFactory.INSTANCE.build(CLASS_CACHE)

    @Synchronized
    fun <T> put(clazz: Class<*>, o: T) {
        classCache.put(clazz, o)
    }

    @Synchronized
    operator fun <T> get(clazz: Class<*>): T? {
        return classCache[clazz] as T?
    }

    @Synchronized
    fun <T> remove(clazz: Class<*>): T? {
        return classCache.remove(clazz) as T?
    }

    @Synchronized
    fun clear() {
        classCache.clear()
    }

}