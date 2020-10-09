package com.xiaojinzi.component.anno;

import com.xiaojinzi.component.anno.support.UiThreadCreateAnno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个接口
 */
@UiThreadCreateAnno
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface ServiceAnno {

    /**
     * 这个服务对应的接口
     */
    Class[] value();

    /**
     * 是否是单例,默认是单例模式的
     */
    boolean singleTon() default true;

}
