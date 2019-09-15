package com.xiaojinzi.component.error;

/**
 * 路由框架的运行时异常, 需要奔溃给用户看, 不能处理的
 */
public class RouterRuntimeException extends RuntimeException {

    public RouterRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
