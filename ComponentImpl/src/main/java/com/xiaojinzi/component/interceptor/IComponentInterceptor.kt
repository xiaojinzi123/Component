package com.xiaojinzi.component.interceptor

import androidx.annotation.UiThread
import com.xiaojinzi.component.impl.RouterInterceptor

/**
 * 拦截器顶级接口
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
interface IComponentInterceptor {

    /**
     * 根据拦截器的唯一名字获取拦截器
     *
     * @param name if name is null,return null
     */
    @UiThread
    fun getByName(name: String): RouterInterceptor?

}