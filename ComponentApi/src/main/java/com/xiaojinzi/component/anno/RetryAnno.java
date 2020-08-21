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
@Target({ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RetryAnno {

    /**
     * 这个 Fragment 对应的唯一 ID
     *
     * @return 对应 Fragment 的一个标记, 不能重复
     */
    String[] value();

}
