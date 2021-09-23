package com.xiaojinzi.component.impl.interceptor

import com.xiaojinzi.component.Component.requiredConfig
import com.xiaojinzi.component.impl.RouterInterceptor
import com.xiaojinzi.component.impl.interceptor.OpenOnceInterceptor
import com.xiaojinzi.component.error.ignore.NavigationFailException
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

/**
 * 这个拦截器必须在其他任何一个拦截器之前执行
 * 从根本上限制同一个界面在一秒钟内只能打开一次,这个拦截器会被框架最先执行
 * note: 这个拦截器没有连同 [Uri.getScheme] 一起判断,其实应该一起的,
 * 但是现实中应该也不会出现一秒钟 host 和 path 都相同的两次路由了
 *
 *
 * time   : 2019/01/23
 *
 * @author : xiaojinzi
 */
object OpenOnceInterceptor : RouterInterceptor {

    private val map: MutableMap<String, Long> = HashMap()

    @Throws(Exception::class)
    override fun intercept(chain: RouterInterceptor.Chain) {
        val uri = chain.request().uri
        val hostAndPath = StringBuffer()
                .append(uri.host)
                .append("/").append(uri.path)
                .toString()
        // 调试的情况下可能会失效,因为你断点打到这里慢慢的往下走那么可能时间已经过了一秒,就失去了限制的作用
        val currentTime = System.currentTimeMillis()
        // 如果之前有了并且时间少于一定的时间
        if (map.containsKey(hostAndPath) && currentTime - map[hostAndPath]!! < requiredConfig().routeRepeatCheckDuration) {
            chain.callback().onError(NavigationFailException("same request can't launch twice in a second, target uri is：$uri"))
        } else {
            map[hostAndPath] = currentTime
            // 放过执行
            chain.proceed(chain.request())
        }
        cleanOverdue()
    }

    private fun cleanOverdue() {
        val currentTime = System.currentTimeMillis()
        val keys: MutableList<String> = ArrayList()
        for (key in map.keys) {
            if (currentTime - map[key]!! >= requiredConfig().routeRepeatCheckDuration) {
                keys.add(key)
            }
        }
        for (key in keys) {
            map.remove(key)
        }
    }

}