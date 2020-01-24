package com.xiaojinzi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个自动注入的注解, 表示注入 Android 中的 Uri
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface UriAutowiredAnno {
}
