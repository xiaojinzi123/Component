package com.xiaojinzi.component.error;

/**
 * 创建拦截器失败,这个错误不需要忽略,在 debug 的时候应该被抛出
 * time   : 2019/01/10
 *
 * @author : xiaojinzi
 */
public class CreateInterceptorException extends RuntimeException {

    public CreateInterceptorException() {
    }

    public CreateInterceptorException(String message) {
        super(message);
    }

    public CreateInterceptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateInterceptorException(Throwable cause) {
        super(cause);
    }

}
