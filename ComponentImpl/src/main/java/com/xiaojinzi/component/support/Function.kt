package com.xiaojinzi.component.support

import java.lang.Exception

interface Function<T, R> {
    /**
     * 做一个转化,从一个对象变成另一个对象
     */
    @Throws(Exception::class)
    fun apply(t: T): R
}