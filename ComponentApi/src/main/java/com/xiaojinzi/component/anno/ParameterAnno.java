package com.xiaojinzi.component.anno;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个元素是一个参数, value 表示参数的名称或者 key
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.CLASS)
public @interface ParameterAnno {

    /**
     * 参数对应的 key
     *
     * @return 参数对应的 key
     */
    String value();

}
