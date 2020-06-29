package com.xiaojinzi.component.support;

import android.support.annotation.Nullable;

/**
 * @param <T>
 * @author xiaojinzi
 */
public interface Callable1<T> {

    /**
     * 获取实际的兑现
     *
     * @return 获取实现对象
     */
    @Nullable
    T get();

}
