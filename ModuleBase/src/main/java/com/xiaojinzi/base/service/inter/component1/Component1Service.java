package com.xiaojinzi.base.service.inter.component1;

import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import io.reactivex.Single;

@MainThread
public interface Component1Service {

    Fragment getFragment();

    void doSomeThing();

    Single<String> testError() throws Exception;

}
