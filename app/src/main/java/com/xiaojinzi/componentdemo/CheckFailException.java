package com.xiaojinzi.componentdemo;

/**
 * 验证失败异常
 */
public class CheckFailException extends RuntimeException {

    public CheckFailException() {
        this("参数验证失败");
    }

    public CheckFailException(String message) {
        super(message);
    }

    public CheckFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckFailException(Throwable cause) {
        super(cause);
    }

}
