package com.xiaojinzi.component.support

import com.xiaojinzi.component.anno.support.CheckClassNameAnno

@CheckClassNameAnno
interface Function1<T, R> {

    /**
     * 做一个转化,从一个对象变成另一个对象
     */
    fun apply(t: T): R

}