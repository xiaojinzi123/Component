package com.xiaojinzi.component.support;

import androidx.annotation.Nullable;

/**
 * @param <T>
 */
public interface CallNullable<T> {

    /**
     * 获取实际的兑现
     *
     * @return 获取实现对象
     */
    @Nullable
    T get();

}
