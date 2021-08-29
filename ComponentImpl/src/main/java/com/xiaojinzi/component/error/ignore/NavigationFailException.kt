package com.xiaojinzi.component.error.ignore;

/**
 * 表示路由的过程中发生异常
 *
 * time   : 2018/11/03
 *
 * @author : xiaojinzi
 */
public class NavigationFailException extends RuntimeException {

    public NavigationFailException() {
    }

    public NavigationFailException(String message) {
        super(message);
    }

    public NavigationFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public NavigationFailException(Throwable cause) {
        super(cause);
    }

}
