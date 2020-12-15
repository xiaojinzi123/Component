package com.xiaojinzi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个自动注入的注解,表示一切可注入的场景,比如：
 * 1.路由目标 Activity 的字段
 * 2.路由目标 Activity 的跨组件的服务
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface ServiceAutowiredAnno {

    String name() default "";

}
