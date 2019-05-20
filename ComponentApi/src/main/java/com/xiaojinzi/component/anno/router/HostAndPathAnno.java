package com.xiaojinzi.component.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个标识某一个接口方法是对应哪个 host 和 path
 *
 * @see HostAnno
 * @see PathAnno
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface HostAndPathAnno {

    /**
     * 表示路由的 host + path
     *
     * @return
     */
    String value();

}
