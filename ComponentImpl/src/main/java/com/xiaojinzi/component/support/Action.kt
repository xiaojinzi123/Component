package com.xiaojinzi.component.support

/**
 * 一个回调在主线程的 Action
 * time   : 2019/01/07
 *
 * @author : xiaojinzi
 */
interface Action {

    /**
     * 需要执行的动作
     *
     * @throws Exception 允许执行的时候抛出一个异常
     */
    fun run()

}