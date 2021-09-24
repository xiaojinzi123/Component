package com.xiaojinzi.component.support

import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import java.lang.Exception

/**
 * 表示一个接受一个参数的接口
 */
@CheckClassNameAnno
interface Consumer<T> {

    /**
     * 接受一个参数的方法,允许抛出异常
     *
     * @param t 方法的参数值
     * @throws Exception 如果发生异常的时候允许抛出异常
     */
    @Throws(Exception::class)
    fun accept(t: T)

}