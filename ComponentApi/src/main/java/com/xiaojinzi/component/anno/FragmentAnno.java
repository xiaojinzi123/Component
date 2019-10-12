package com.xiaojinzi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个 Fragment, 方便路由到此注解标记的 Fragment
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface FragmentAnno {

    /**
     * 这个 Fragment 对应的唯一 ID
     *
     * @return 对应 Fragment 的一个标记, 不能重复
     */
    String[] value();

    /**
     * 是否是单例,默认是单例模式的
     *
     * @return
     */
    boolean singleTon() default false;

}
