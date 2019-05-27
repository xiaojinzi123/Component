package com.xiaojinzi.component.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由跳转的时候,给跳转添加一个 options,类型是 Bundle
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.CLASS)
public @interface OptionsAnno {
}
