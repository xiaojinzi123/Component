package com.xiaojinzi.component.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 详情解释请看 {@link UserInfoAnno}
 *
 * @see UserInfoAnno
 * @see HostAnno
 * @see PathAnno
 * @see HostAndPathAnno
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface HostAndPathAnno {

    /**
     * 表示路由的 host + path
     */
    String value();

}
