package com.ehi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拦截器的注解,用这个注解标记的不是全局的拦截器,但是你可以使用特定的字符串访问到这个拦截器
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface EHiInterceptorAnno {

    /**
     * 拦截器的名字,这个不能重复
     *
     * @return
     */
    String value();

}
