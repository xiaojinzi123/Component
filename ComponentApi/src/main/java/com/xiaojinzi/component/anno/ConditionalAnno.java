package com.xiaojinzi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一组条件, 当条件成立, 对应的功能才起作用
 * 目前仅支持混用的是：
 * {@link ConditionalAnno} 和 {@link GlobalInterceptorAnno}
 * {@link ConditionalAnno} 和 {@link InterceptorAnno}
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ConditionalAnno {

    /**
     * 指定多个条件的类,指定的这些类必须实现 {@link com.xiaojinzi.component.support.Condition} 接口
     * 所有都返回了 true 才表示条件成立
     */
    Class[] conditions();

}
