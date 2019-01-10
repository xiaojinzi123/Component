package com.ehi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个路由的 Api
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface EHiRouterAnno {

    /**
     * 定义host
     *
     * @return
     */
    String host() default "";

    /**
     * 路径
     *
     * @return
     */
    String value();

    /**
     * 拦截器的地址
     *
     * @return
     */
    Class[] interceptors() default {};

    /**
     * 拦截器的名字的列表,可以指定其他模块的拦截器
     * 这点是跨模块的,很棒的
     *
     * @return
     */
    String[] interceptorNames() default {};

    /**
     * 描述信息
     *
     * @return
     */
    String desc() default "";

}
