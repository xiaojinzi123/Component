package com.ehi.component.error;

/**
 * 表示不知道是啥异常的异常
 *
 * time   : 2018/11/03
 *
 * @author : xiaojinzi 30212
 */
public class UnknowException extends RuntimeException {

    public UnknowException() {
    }

    public UnknowException(String message) {
        super(message);
    }

    public UnknowException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknowException(Throwable cause) {
        super(cause);
    }

}
