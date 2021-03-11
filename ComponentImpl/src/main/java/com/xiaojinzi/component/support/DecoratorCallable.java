package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

/**
 * @author : xiaojinzi
 */
@CheckClassNameAnno
public interface DecoratorCallable<T> {

    /**
     * 装饰目标对象
     *
     * @return 返回装饰者
     */
    @NonNull
    T get(@NonNull T target);

    /**
     * 优先级, 值越大, 对应的装饰者执行的顺序越靠前
     */
    int priority();

}
