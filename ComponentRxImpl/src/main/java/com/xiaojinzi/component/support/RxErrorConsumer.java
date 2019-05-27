package com.xiaojinzi.component.support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.functions.Consumer;

/**
 * 框架结合 RxJava后的错误的忽略处理,如果使用中没有处理,那么这里默认不处理
 * <p>
 * time   : 2018/11/20
 *
 * @author : xiaojinzi 30212
 */
public class RxErrorConsumer<T extends Throwable> implements Consumer<T> {

    /**
     * 如果使用者不想处理错误的话,这些错误都可以被默认忽略
     */
    private final Class[] defaultIgnoreError;

    @Nullable
    private Consumer<T> preConsumer = null;

    public RxErrorConsumer(Consumer<T> preConsumer, Class[] defaultIgnoreErrors) {
        this.preConsumer = preConsumer;
        this.defaultIgnoreError = defaultIgnoreErrors;
    }

    @Override
    public void accept(T throwable) throws Exception {
        if (isIgnore(throwable)) {
            return;
        }
        if (preConsumer != null) {
            preConsumer.accept(throwable);
            return;
        }
        if (throwable instanceof Exception) {
            throw ((Exception) throwable);
        } else {
            throw new OnErrorNotImplementedException(throwable);
        }
    }

    public boolean isIgnore(@NonNull Throwable throwable) {
        Throwable currThrowable = throwable;
        if (defaultIgnoreError != null) {
            while (currThrowable != null) {
                for (Class errorClass : defaultIgnoreError) {
                    if (currThrowable.getClass() == errorClass) {
                        return true;
                    }
                }
                // 拿到 cause,接着判断
                currThrowable = currThrowable.getCause();
            }
        }
        return false;
    }

}
