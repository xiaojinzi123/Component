package com.ehi.component;

/**
 * 声明一个执行过程出现的需要抛出的异常
 * time   : 2019/02/21
 *
 * @author : xiaojinzi 30212
 */
public class ProcessException extends RuntimeException {

    public ProcessException() {
    }

    public ProcessException(String s) {
        super(s);
    }

    public ProcessException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ProcessException(Throwable throwable) {
        super(throwable);
    }

}
