package com.xiaojinzi.component.anno;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在自定义跳转和自定义 Intent 的时候,可以一个这个注解来标记自定义的参数
 * 目前支持自动生成的包括：
 * byte short int long float double boolean String Serializable Parcelable
 * 这个注解同时也是可以标记在 Activity 的字段上的,但是字段不能是 private 类型的
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
