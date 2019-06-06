package com.xiaojinzi.component.support;

import androidx.annotation.NonNull;

/**
 * 表示一个接受2个参数的接口
 *
 * @param <T1>
 * @param <T2>
 */
public interface Consumer2<T1, T2> {

    /**
     * 接受一个参数的方法,允许抛出异常
     *
     * @param t1 第1个参数值
     * @param t2 第2个参数值
     * @throws Exception 如果发生异常的时候允许抛出异常
     */
    void accept(T1 t1, T2 t2) throws Exception;

}