package com.ehi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个拦截器的注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface EHiInterceptorAnno {

    /**
     * 这个路由器的级别,如果越高顺序就会越靠前
     */
    int priority();

}
