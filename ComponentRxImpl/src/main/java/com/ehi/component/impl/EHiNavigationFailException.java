package com.ehi.component.impl;

/**
 * time   : 2018/11/03
 *
 * @author : xiaojinzi 30212
 */
public class EHiNavigationFailException extends Exception {

    public EHiNavigationFailException() {
    }

    public EHiNavigationFailException(String message) {
        super(message);
    }

    public EHiNavigationFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public EHiNavigationFailException(Throwable cause) {
        super(cause);
    }

}
