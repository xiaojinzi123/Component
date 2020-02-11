package com.xiaojinzi.component.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对于接口中的路有方法来说,使用一组拦截器
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface UseInteceptorAnno {

    /**
     * 拦截器的名称
     */
    String[] names() default {};

    /**
     * 拦截器的 class
     */
    Class[] classes() default {};

}
