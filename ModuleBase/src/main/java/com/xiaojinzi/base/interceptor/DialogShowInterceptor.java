package com.xiaojinzi.base.interceptor;

import android.app.ProgressDialog;
import android.content.Context;

import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.InterceptorAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 你可以声明这个拦截器使用 {@link RouterAnno#interceptors()} 来使用,也可以通过
 * {@link RouterAnno#interceptorNames()} ()} 配合 {@link InterceptorAnno}
 * 来使用
 * time   : 2018/12/04
 *
 * @author : xiaojinzi
 */
public class DialogShowInterceptor implements RouterInterceptor {

    @Override
    public void intercept(final Chain chain) throws Exception {
        Context rawContext = chain.request().getRawContext();
        if (rawContext == null) {
            chain.callback().onError(new Exception("context is null"));
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(rawContext, "温馨提示", "耗时操作进行中,2秒后结束", true, false);
        dialog.show();
        Disposable disposable = Completable.complete()
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnEvent(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dialog.dismiss();
                    }
                })
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        chain.proceed(chain.request());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        chain.callback().onError(new Exception("error"));
                    }
                });

    }

}
