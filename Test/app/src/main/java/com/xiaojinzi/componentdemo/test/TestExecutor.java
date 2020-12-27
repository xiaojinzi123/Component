package com.xiaojinzi.componentdemo.test;

import io.reactivex.Completable;

public interface TestExecutor {

    /**
     * 执行
     *
     * @param context
     */
    Completable execute(TestContext context);

}
