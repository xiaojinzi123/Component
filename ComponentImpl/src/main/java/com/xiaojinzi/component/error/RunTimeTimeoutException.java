package com.xiaojinzi.component.error;

public class RunTimeTimeoutException extends RuntimeException {

    public RunTimeTimeoutException() {
    }

    public RunTimeTimeoutException(String message) {
        super(message);
    }

    public RunTimeTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public RunTimeTimeoutException(Throwable cause) {
        super(cause);
    }

}
