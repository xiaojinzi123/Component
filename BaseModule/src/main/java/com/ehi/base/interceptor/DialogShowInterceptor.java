package com.ehi.base.interceptor;

import android.app.ProgressDialog;
import android.content.Context;

import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouterInterceptor;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 你可以声明这个拦截器使用 {@link EHiRouterAnno#interceptors()} 来使用,也可以通过
 * {@link EHiRouterAnno#interceptorNames()} ()} 配合 {@link com.ehi.component.anno.EHiInterceptorAnno}
 * 来使用
 * time   : 2018/12/04
 *
 * @author : xiaojinzi 30212
 */
public class DialogShowInterceptor implements EHiRouterInterceptor {

    @Override
    public void intercept(final Chain chain) throws Exception {

        Context rawContext = chain.request().getRawContext();

        if (rawContext == null) {
            chain.callback().onError(new Exception("context is null"));
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(rawContext, "温馨提示", "耗时操作进行中,2秒后结束", true, false);

        dialog.show();

        Single
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "test";
                    }
                })
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnEvent(new BiConsumer<String, Throwable>() {
                    @Override
                    public void accept(String s, Throwable throwable) throws Exception {
                        dialog.dismiss();
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
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
