package com.xiaojinzi.component.support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

/**
 * 单例形式
 */
@CheckClassNameAnno
public abstract class SingletonFunction1<T, R> implements Function1<T, R> {

    @Nullable
    private volatile R instance;

    @NonNull
    @Override
    public R apply(@NonNull T t) {
        if (null == instance) {
            synchronized (this) {
                if (null == instance) {
                    instance = applyRaw(t);
                }
            }
        }
        return instance;
    }

    /**
     * 获取真正的对象
     */
    @NonNull
    protected abstract R applyRaw(T t);

}