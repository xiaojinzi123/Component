package com.xiaojinzi.base.service.inter.component1;

import android.support.annotation.MainThread;
import android.support.v4.app.Fragment;

import io.reactivex.Single;

@MainThread
public interface Component1Service {

    Fragment getFragment();

    void doSomeThing();

    Single<String> testError() throws Exception;

}
