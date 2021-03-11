package com.xiaojinzi.component.anno.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个资源不能在项目中使用, 是框架内部使用的
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface NotAppUseAnno {
    String value() default "";
}
