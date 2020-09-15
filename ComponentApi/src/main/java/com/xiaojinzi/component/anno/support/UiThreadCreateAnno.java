package com.xiaojinzi.component.anno.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1. 如果标记的是一个普通的类, 表示此类会在主线程中被创建
 * 2. 如果标记的是一个注解, 表示被标记的注解标记的所有类都是在主线程中被创建
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface UiThreadCreateAnno {
}
