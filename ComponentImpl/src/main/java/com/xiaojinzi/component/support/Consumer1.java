package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;

/**
 * 表示一个接受一个参数的接口
 *
 * @param <T>
 */
public interface Consumer1<T> {

    /**
     * 接受一个参数的方法,允许抛出异常
     *
     * @param t 方法的参数值
     */
    void accept(@NonNull T t);

}