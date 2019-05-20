package com.xiaojinzi.component.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记这个方法是普通的 navigate 方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface NavigateAnno {

    /**
     * 为了拿 ActivityResult
     *
     * @return
     */
    boolean forResult() default false;

    /**
     * 为了拿 Intent
     *
     * @return
     */
    boolean forIntent() default false;

    /**
     * 为了那 resultCode
     *
     * @return
     */
    boolean forResultCode() default false;

    /**
     * 当你使用了 {@link #forIntent()}的时候,你可以使用这个属性匹配 ResultCode
     *
     * @return
     */
    int resultCodeMatch() default Integer.MIN_VALUE;

}
