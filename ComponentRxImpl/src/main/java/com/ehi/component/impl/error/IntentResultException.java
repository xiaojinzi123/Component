package com.ehi.component.impl.error;

/**
 * 表示 Intent 获取过程中发生异常
 *
 * time   : 2018/11/03
 *
 * @author : xiaojinzi 30212
 */
public class IntentResultException extends Exception {

    public IntentResultException() {
    }

    public IntentResultException(String message) {
        super(message);
    }

    public IntentResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntentResultException(Throwable cause) {
        super(cause);
    }

}
