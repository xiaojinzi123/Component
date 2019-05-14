package com.xiaojinzi.component.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个标识某一个接口方法是对应什么路径
 *
 * @see HostAnno
 * @see HostAndPathAnno
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface PathAnno {

    /**
     * 表示路由的 path
     *
     * @return
     */
    String value();

}
