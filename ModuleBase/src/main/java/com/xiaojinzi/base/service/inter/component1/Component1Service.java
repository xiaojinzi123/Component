package com.xiaojinzi.base.service.inter.component1;

import android.support.annotation.MainThread;

import io.reactivex.Single;

@MainThread
public interface Component1Service {

    void doSomeThing();

    Single<String> testError() throws Exception;

}
