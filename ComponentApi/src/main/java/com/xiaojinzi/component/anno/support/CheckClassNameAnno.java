package com.xiaojinzi.component.anno.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个类需要注意它的类名改变!
 * {@link com.xiaojinzi.component.ComponentConstants} 这个类中的每一个全类名都应该被关注
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface CheckClassNameAnno {
    String value() default "";
}
