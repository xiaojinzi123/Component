package com.xiaojinzi.component.support;

/**
 * 每一个生成的类都应该实现这个接口
 */
public interface ParameterInject<T> {

    /**
     * 注入控件
     */
    void inject(T target);

}
