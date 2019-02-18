package com.ehi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 全局拦截器的注解,用这个注解的拦截器在 App 启动的时候就会被加载到拦截器列表中
 * 可以拦截到所有的路由请求
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface EHiGlobalInterceptorAnno {

    /**
     * 定义优先级,值越大优先级越高
     *
     * @return
     */
    int priority() default 0;

}
