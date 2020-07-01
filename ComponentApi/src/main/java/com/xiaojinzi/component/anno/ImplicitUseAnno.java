package com.xiaojinzi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示隐式的使用了
 */
@Retention(RetentionPolicy.SOURCE)
@Target({
        ElementType.TYPE, ElementType.METHOD,
        ElementType.FIELD, ElementType.PARAMETER
})
public @interface ImplicitUseAnno {
}
