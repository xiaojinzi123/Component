package com.xiaojinzi.component.support

/**
 * 模块管理中心注册的类
 */
interface IComponentCenter<T> {

    /**
     * 注册
     *
     * @param t 注册的目标
     */
    fun register(t: T)

    /**
     * 通过host注册
     *
     * @param host 需要注册的 host
     */
    fun register(host: String)

    /**
     * 反注册
     *
     * @param t 反注册的目标
     */
    fun unregister(t: T)

    /**
     * 通过 host 反注册
     *
     * @param host 反注册的 host
     */
    fun unregister(host: String)

}