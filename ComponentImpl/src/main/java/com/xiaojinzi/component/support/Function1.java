package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

@CheckClassNameAnno
public interface Function1<T, R> {

    /**
     * 做一个转化,从一个对象变成另一个对象
     */
    @NonNull
    R apply(@NonNull T t);

}