package com.xiaojinzi.component.error;

/**
 * 表示使用RxJava发射的错误,cause中是确切的错误对象
 * time   : 2019/01/09
 *
 * @author : xiaojinzi
 */
public class RxJavaException extends Exception {

    public RxJavaException() {
    }

    public RxJavaException(String message) {
        super(message);
    }

    public RxJavaException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxJavaException(Throwable cause) {
        super(cause);
    }

}
