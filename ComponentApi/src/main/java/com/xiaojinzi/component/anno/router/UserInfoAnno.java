package com.xiaojinzi.component.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * userInfo 表示 Android 中的 Uri 的 UserInfo
 * host 表示 Android 中的 Uri 的 host
 * userInfo + @ + host = Android 中的 Uri 的 authority
 * 比如 root@xiaojinzi.com
 * userInfo = "root"
 * host = "xiaojinzi.com"
 * authority = "root@xiaojinzi.com"
 *
 * @see UserInfoAnno
 * @see HostAnno
 * @see PathAnno
 * @see HostAndPathAnno
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface UserInfoAnno {
    String value();
}
