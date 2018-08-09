package com.ehi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个路由的 Api
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface EHiRouter {

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
     * 描述信息
     *
     * @return
     */
    String desc() default "";

    /**
     * 级别,如果两个组件的路径一样,级别越高,就会先跳转
     */
    //int priority() default -1;

}
