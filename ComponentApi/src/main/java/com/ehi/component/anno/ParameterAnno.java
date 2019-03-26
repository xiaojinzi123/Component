package com.ehi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在自定义跳转和自定义 Intent 的时候,可以一个这个注解来标记自定义的参数
 * 目前支持自动生成的包括：
 * byte short int long float double boolean String Serializable Parcelable
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.CLASS)
public @interface ParameterAnno {

    /**
     * 参数对应的 key
     *
     * @return
     */
    String value();

    /**
     * byte 的默认值
     *
     * @return
     */
    byte byteDefault() default 0;

    /**
     * short 的默认值
     *
     * @return
     */
    short shortDefault() default 0;

    /**
     * int 的默认值
     *
     * @return
     */
    int intDefault() default 0;

    /**
     * long 的默认值
     *
     * @return
     */
    int longDefault() default 0;

    /**
     * float 的默认值
     *
     * @return
     */
    float floatDefault() default 0;

    /**
     * double 的默认值
     *
     * @return
     */
    double doubleDefault() default 0;

    /**
     * boolean 的默认值
     *
     * @return
     */
    boolean booleanDefault() default false;

}
