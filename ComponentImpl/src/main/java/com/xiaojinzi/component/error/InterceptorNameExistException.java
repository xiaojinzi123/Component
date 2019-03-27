package com.xiaojinzi.component.error;

/**
 * 拦截器的名称已经存在的异常
 * time   : 2019/01/10
 *
 * @author : xiaojinzi 30212
 */
public class InterceptorNameExistException extends RuntimeException {

    public InterceptorNameExistException() {
    }

    public InterceptorNameExistException(String message) {
        super(message);
    }

    public InterceptorNameExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterceptorNameExistException(Throwable cause) {
        super(cause);
    }

}
