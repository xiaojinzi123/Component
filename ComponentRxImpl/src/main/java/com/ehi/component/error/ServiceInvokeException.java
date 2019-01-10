package com.ehi.component.error;

/**
 * time   : 2019/01/09
 *
 * @author : xiaojinzi 30212
 */
public class ServiceInvokeException extends Exception  {

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
