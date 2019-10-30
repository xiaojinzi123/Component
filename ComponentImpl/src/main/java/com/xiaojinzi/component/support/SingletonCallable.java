package com.xiaojinzi.component.support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassName;

/**
 * 单例服务,这是注册服务默认的形式
 *
 * @param <T>
 */
@CheckClassName
public abstract class SingletonCallable<T> implements Callable<T> {

    @Nullable
    private volatile T instance;

    @Override
    public final T get() {
        if (null == instance) {
            synchronized (this) {
                if (null == instance) {
                    instance = getRaw();
                }
            }
        }
        return instance;
    }

    /**
     * 获取真正的对象
     *
     * @return
     */
    @NonNull
    protected abstract T getRaw();

}