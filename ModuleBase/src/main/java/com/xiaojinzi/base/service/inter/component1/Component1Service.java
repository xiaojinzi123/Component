package com.xiaojinzi.base.service.inter.component1;

import android.support.annotation.MainThread;

import com.xiaojinzi.component.anno.ServiceMethodAnno;
import com.xiaojinzi.component.anno.ServiceParameterAnno;

import io.reactivex.Single;

@MainThread
public interface Component1Service {

    @ServiceMethodAnno("doSomeThing")
    void doSomeThing(
            @ServiceParameterAnno("userId") int userId
    );

    Single<String> testError() throws Exception;

}
