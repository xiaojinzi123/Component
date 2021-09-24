package com.xiaojinzi.component.support

import com.xiaojinzi.component.anno.support.CheckClassNameAnno

/**
 * @author : xiaojinzi
 */
@CheckClassNameAnno
interface DecoratorCallable<T> {
    /**
     * 装饰目标对象
     *
     * @return 返回装饰者
     */
    fun get(target: T): T

    /**
     * 优先级, 值越大, 对应的装饰者执行的顺序越靠前
     */
    fun priority(): Int
}