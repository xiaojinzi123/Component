package com.xiaojinzi.component.anno.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记所有 Component 生成的类. 自动生成的类会带上此注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ComponentGeneratedAnno {
    String value() default "";
}
