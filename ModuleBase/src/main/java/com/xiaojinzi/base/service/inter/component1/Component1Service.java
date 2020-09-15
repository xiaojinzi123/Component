package com.xiaojinzi.base.service.inter.component1;

import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

import io.reactivex.Single;

@UiThread
public interface Component1Service {

    void doSomeThing();

    Single<String> testError() throws Exception;

}
