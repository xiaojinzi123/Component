package com.xiaojinzi.component.anno;

import com.xiaojinzi.component.anno.defvalue.StringDefaultAnno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在自定义跳转和自定义 Intent 的时候,可以一个这个注解来标记自定义的参数
 * 目前支持自动生成的包括：
 * byte short int long float double boolean String Serializable Parcelable
 * 这个注解同时也是可以标记在 Activity 的字段上的,但是字段不能是 private 类型的
 *
 * @see StringDefaultAnno 表示给字段或者参数添加一个默认值
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface ParameterAnno {

    /**
     * 参数对应的 key
     * for string {@link StringDefaultAnno}
     *
     * @return
     */
    String value();

}
