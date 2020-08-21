package com.xiaojinzi.component.anno;

import com.xiaojinzi.component.anno.support.MainThreadCreateAnno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 重试注解
 */
@MainThreadCreateAnno
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RetryAnno {

    /**
     * 大于 0 才是有效的
     */
    int value() default 0;

}
