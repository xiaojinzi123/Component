package com.xiaojinzi.component.error.ignore;

/**
 * 运行在非主线程异常
 * time   : 2019/01/10
 *
 * @author : xiaojinzi 30212
 */
public class NotRunOnMainThreadException extends RuntimeException {

    public NotRunOnMainThreadException() {
    }

    public NotRunOnMainThreadException(String message) {
        super(message);
    }

    public NotRunOnMainThreadException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotRunOnMainThreadException(Throwable cause) {
        super(cause);
    }

}
