package com.xiaojinzi.component.error;

/**
 * time   : 2019/01/09
 *
 * @author : xiaojinzi
 */
public class ServiceInvokeException extends RuntimeException  {

    public ServiceInvokeException() {
    }

    public ServiceInvokeException(String message) {
        super(message);
    }

    public ServiceInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceInvokeException(Throwable cause) {
        super(cause);
    }

}
