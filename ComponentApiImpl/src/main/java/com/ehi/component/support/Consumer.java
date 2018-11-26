package com.ehi.component.support;

/**
 * 表示一个接受一个参数的接口
 *
 * @param <T>
 */
public interface Consumer<T> {

    /**
     * Consume the given value.
     *
     * @param t the value
     * @throws Exception on error
     */
    void accept(T t) throws Exception;

}