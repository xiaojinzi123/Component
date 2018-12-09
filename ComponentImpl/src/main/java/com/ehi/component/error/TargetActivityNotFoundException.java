package com.ehi.component.error;

/**
 * 表示目标界面没有找到
 *
 * time   : 2018/11/11
 *
 * @author : xiaojinzi 30212
 */
public class TargetActivityNotFoundException extends RuntimeException {

    public TargetActivityNotFoundException() {
    }

    public TargetActivityNotFoundException(String message) {
        super(message);
    }

    public TargetActivityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetActivityNotFoundException(Throwable cause) {
        super(cause);
    }

}
