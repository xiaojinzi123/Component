package com.xiaojinzi.component.anno.defvalue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用这个注解给参数或者字段添加上一个默认值
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.CLASS)
public @interface FloatDefaultAnno {

    /**
     * 使用这个注解给参数或者字段添加上一个默认值,value是这个字段或者参数默认的值
     *
     * @return
     */
    float value();

}
