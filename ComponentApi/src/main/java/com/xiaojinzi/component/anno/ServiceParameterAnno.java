package com.xiaojinzi.component.anno;

import com.xiaojinzi.component.anno.support.MainThreadCreateAnno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个 Service 服务接口的方法的参数是暴露的
 */
@MainThreadCreateAnno
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.CLASS)
public @interface ServiceParameterAnno {

    /**
     * 名称
     */
    String value();

}
