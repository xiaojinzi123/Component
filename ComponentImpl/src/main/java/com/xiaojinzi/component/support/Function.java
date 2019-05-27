package com.xiaojinzi.component.support;

import androidx.annotation.NonNull;

public interface Function<T, R> {

    /**
     * 做一个转化,从一个对象变成另一个对象
     *
     * @param t
     * @return
     * @throws Exception
     */
    @NonNull
    R apply(@NonNull T t) throws Exception;

}