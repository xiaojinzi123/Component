package com.xiaojinzi.component.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个标识某一个接口方法是对应那个模块的
 *
 * @see PathAnno
 * @see HostAndPathAnno
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface HostAnno {

    /**
     * 表示路由的 host
     *
     * @return
     */
    String value();

}
