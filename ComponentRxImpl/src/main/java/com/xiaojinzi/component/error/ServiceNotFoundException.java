package com.xiaojinzi.component.error;

/**
 * time   : 2019/01/09
 *
 * @author : xiaojinzi 30212
 */
public class ServiceNotFoundException extends RuntimeException  {
    public ServiceNotFoundException() {
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }

    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceNotFoundException(Throwable cause) {
        super(cause);
    }
}
