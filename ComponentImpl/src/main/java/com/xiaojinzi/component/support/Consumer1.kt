package com.xiaojinzi.component.support

/**
 * 表示一个接受一个参数的接口
 *
 * @param <T>
</T> */
interface Consumer1<T> {

    /**
     * 接受一个参数的方法, 不允许抛出异常
     *
     * @param t 方法的参数值
     */
    fun accept(t: T)

}