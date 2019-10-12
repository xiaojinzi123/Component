package com.xiaojinzi.component.support;

import androidx.annotation.NonNull;

/**
 * 懒加载设计
 * time   : 2018/11/27
 *
 * @author : xiaojinzi 30212
 */
public interface Callable<T> {

    /**
     * 获取实际的兑现
     *
     * @return 获取实现对象
     */
    @NonNull
    T get();

}
