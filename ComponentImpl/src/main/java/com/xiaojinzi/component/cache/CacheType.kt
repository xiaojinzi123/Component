package com.xiaojinzi.component.cache

import android.app.ActivityManager
import android.content.Context
import com.xiaojinzi.component.support.Utils

/**
 * 构建 [Cache] 时,使用 [CacheType] 中声明的类型,来区分不同的模块
 * 从而为不同的模块构建不同的缓存策略
 */
interface CacheType {

    companion object {
        const val CLASS_CACHE_TYPE_ID = 0
        val CLASS_CACHE: CacheType = object : CacheType {

            private val MAX_SIZE = 25

            override val cacheTypeId: Int
                get() = CLASS_CACHE_TYPE_ID

            override fun calculateCacheSize(context: Context): Int {
                Utils.checkNullPointer(context, "context")
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val targetMemoryCacheSize: Int = if (Utils.isLowMemoryDevice(activityManager)) {
                    activityManager.memoryClass / 6
                } else {
                    activityManager.memoryClass / 4
                }
                return if (targetMemoryCacheSize > MAX_SIZE) {
                    MAX_SIZE
                } else targetMemoryCacheSize
            }
        }
    }

    /**
     * 返回框架内需要缓存的模块对应的 `id`
     */
    val cacheTypeId: Int

    /**
     * 计算对应模块需要的缓存大小
     */
    fun calculateCacheSize(context: Context): Int



}