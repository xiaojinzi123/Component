package com.xiaojinzi.component.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个方法的跳转类型. 默认可省略
 * 返回值支持以下的类型:
 * 1. void
 * 2. {@link com.xiaojinzi.component.ComponentConstants#NAVIGATOR_CLASS_NAME}
 * 3. {@link com.xiaojinzi.component.ComponentConstants#RXJAVA_COMPLETABLE}
 * 4. {@link com.xiaojinzi.component.ComponentConstants#RXJAVA_SINGLE}
 * 此注解省略和 @NavigateAnno 是一样的
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface NavigateAnno {

    /**
     * 为了拿 ActivityResult
     */
    boolean forResult() default false;

    /**
     * 为了拿 Intent
     */
    boolean forIntent() default false;

    /**
     * 为了那 resultCode
     */
    boolean forResultCode() default false;

    /**
     * 当你使用了 {@link #forIntent()}的时候,你可以使用这个属性匹配 ResultCode
     */
    int resultCodeMatch() default Integer.MIN_VALUE;

}
