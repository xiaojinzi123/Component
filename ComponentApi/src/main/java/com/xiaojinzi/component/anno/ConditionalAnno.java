package com.xiaojinzi.component.anno;

import com.xiaojinzi.component.api.Condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个条件
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ConditionalAnno {

    /**
     * 直接能支持的返回 true 和 false 的
     *
     * @return true 表示条件成立
     */
    //boolean value();

    /**
     * 指定多个条件的类,指定的这些类必须实现 {@link Condition} 接口
     * 所有都返回了 true 才表示条件成立
     *
     * @return
     */
    Class<? extends Condition>[] conditions();

}
