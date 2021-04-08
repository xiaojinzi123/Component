package com.xiaojinzi.component.error;

public class ServiceRepeatCreateException extends RuntimeException {

    public ServiceRepeatCreateException() {
    }

    public ServiceRepeatCreateException(String message) {
        super(message);
    }

    public ServiceRepeatCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceRepeatCreateException(Throwable cause) {
        super(cause);
    }

}
