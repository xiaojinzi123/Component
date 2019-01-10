package com.ehi.component.error;

/**
 * time   : 2019/01/10
 *
 * @author : xiaojinzi 30212
 */
public class InterceptorNotFoundException extends RuntimeException {

    public InterceptorNotFoundException() {
    }

    public InterceptorNotFoundException(String message) {
        super(message);
    }

    public InterceptorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterceptorNotFoundException(Throwable cause) {
        super(cause);
    }

}
