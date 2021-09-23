package com.xiaojinzi.component.impl

import android.content.Intent
import com.xiaojinzi.component.support.Utils

/**
 * 这个类表示一次成功路由的返回结果对象
 * time   : 2018/11/10
 *
 * @author : xiaojinzi
 */
data class RouterResult
@JvmOverloads constructor(
        /**
         * 最原始的请求,谁都更改不了的,而且不可能为空在这里
         */
        val originalRequest: RouterRequest,
        /**
         * 获取可能由拦截器修改过的 request 对象,大部分没有被修改的其实就是最原始的 request 对象
         * 如果成功了,这个会有值,这个可能不是最原始的请求啦,可能是拦截器修改过或者
         * 整个 request 对象都被改了
         */
        val finalRequest: RouterRequest,
        /**
         * 如果是跳转为了目标的 Intent
         */
        val targetIntent: Intent? = null
)