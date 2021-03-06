package com.xiaojinzi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个实现类是某些接口的实现
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface ServiceAnno {

    /**
     * 这个服务对应的接口
     */
    Class[] value();

    /**
     * 实现类唯一的名字. debug 的时候可以开启检查
     * 会检查出重名的
     */
    String[] name() default {};

    /**
     * 是否是单例,默认是单例模式的
     */
    boolean singleTon() default true;

}
