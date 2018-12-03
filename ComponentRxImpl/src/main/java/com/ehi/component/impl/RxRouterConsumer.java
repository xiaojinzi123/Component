package com.ehi.component.impl;

import android.support.annotation.Nullable;

import com.ehi.component.error.NavigationFailException;
import com.ehi.component.error.TargetActivityNotFoundException;
import com.ehi.component.error.IntentResultException;
import com.ehi.component.error.UnknowException;

import io.reactivex.functions.Consumer;

/**
 * RxRouter 的默认处理处理方式,如果使用中没有处理,那么这里默认不处理
 *
 * time   : 2018/11/20
 *
 * @author : xiaojinzi 30212
 */
public class RxRouterConsumer implements Consumer<Throwable> {

    @Nullable
    private Consumer<Throwable> preConsumer = null;

    public RxRouterConsumer(Consumer<Throwable> preConsumer) {
        this.preConsumer = preConsumer;
    }

    @Override
    public void accept(Throwable throwable) throws Exception {
        if (throwable instanceof NavigationFailException) {
            return;
        }
        if (throwable instanceof IntentResultException) {
            return;
        }
        if (throwable instanceof TargetActivityNotFoundException) {
            return;
        }
        if (throwable instanceof UnknowException) {
            return;
        }
        if (preConsumer != null) {
            preConsumer.accept(throwable);
            return;
        }
        throw new Exception(throwable);
    }

}
