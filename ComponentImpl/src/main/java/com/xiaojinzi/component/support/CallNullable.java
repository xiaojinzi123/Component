package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

/**
 *
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
