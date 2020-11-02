package com.xiaojinzi.component.anno;

import com.xiaojinzi.component.anno.support.UiThreadCreateAnno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 重试注解
 */
@UiThreadCreateAnno
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryAnno {

    /**
     * 这个服务对应的接口
     */
    int value() default 1;

}
