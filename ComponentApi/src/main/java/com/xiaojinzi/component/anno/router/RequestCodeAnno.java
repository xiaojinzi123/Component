package com.xiaojinzi.component.anno.router;

import com.xiaojinzi.component.ComponentConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果标识某一个方法表示 RequestCode 是什么
 * 如果标记某一个方法中的参数,那么那个参数的值就是requestCode的值
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.CLASS)
public @interface RequestCodeAnno {

    /**
     * requestCode 的值
     * 如果标记在方法上 value 值没有表示随机生成一个
     * 如果标记在参数上,则 value 的值不起作用
     */
    int value() default Integer.MIN_VALUE;

}
