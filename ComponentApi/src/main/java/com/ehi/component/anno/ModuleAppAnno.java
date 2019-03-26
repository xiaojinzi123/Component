package com.ehi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个标识模块的生命周期类的注解,因为用注解标识的类肯定在某一个模块,所以标识了就知道那个类是哪个模块下面的
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ModuleAppAnno {
}
