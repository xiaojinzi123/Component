package com.xiaojinzi.base.service.inter.component1;

import android.support.annotation.UiThread;

import io.reactivex.Single;

@UiThread
public interface Component1Service {

    void doSomeThing();

    Single<String> testError() throws Exception;

}
