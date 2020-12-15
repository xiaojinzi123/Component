package com.xiaojinzi.componentdemo.test;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.support.CallbackAdapter;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

/**
 * 取消的测试
 */
public class CancelTest implements TestExecutor {

    private TestContext mTestContext;

    @Override
    public Completable execute(TestContext testContext) {
        mTestContext = testContext;
        return Completable.concatArray(
                testContext.wrapTask(cancelImmediately()).doOnComplete(() -> testContext.addTaskPassMsg("cancelImmediately")),
                testContext.wrapTask(cancelImmediately1()).doOnComplete(() -> testContext.addTaskPassMsg("cancelImmediately1")),
                testContext.wrapTask(cancelFromActivityWhenActivityFinish()).doOnComplete(() -> testContext.addTaskPassMsg("cancelFromActivityWhenActivityFinish")),
                testContext.wrapTask(cancelFromFragmentWhenActivityFinish()).doOnComplete(() -> testContext.addTaskPassMsg("cancelFromFragmentWhenActivityFinish"))
        );
    }

    public Completable cancelImmediately() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router
                        .with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .putString("data", "cancelImmediately")
                        .navigate(new TestContext.CallbackCancelIsSuccessful(emitter))
                        .cancel();
            }
        });
    }

    public Completable cancelImmediately1() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router
                        .with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCodeRandom()
                        .putString("data", "cancelImmediately1")
                        .navigateForResult(new TestContext.BiCallbackCancelIsSuccessful(emitter))
                        .cancel();
            }
        });
    }

    public Completable cancelFromActivityWhenActivityFinish() {

        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Help.NAME)
                        .path(ModuleConfig.Help.CANCEL_FOR_TEST)
                        .requestCodeRandom()
                        .navigateForResultCodeMatch(new CallbackAdapter() {
                            @Override
                            public void onSuccess(@NonNull RouterResult result) {
                                super.onSuccess(result);
                                emitter.onComplete();
                            }

                            @Override
                            public void onError(RouterErrorResult errorResult) {
                                super.onError(errorResult);
                                emitter.onError(errorResult.getError());
                            }
                        }, Activity.RESULT_OK);
            }
        });

    }

    public Completable cancelFromFragmentWhenActivityFinish() {

        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Help.NAME)
                        .path(ModuleConfig.Help.CANCEL_FOR_TEST)
                        .requestCodeRandom()
                        .putBoolean("isUseFragment", true)
                        .navigateForResultCodeMatch(new CallbackAdapter() {
                            @Override
                            public void onSuccess(@NonNull RouterResult result) {
                                super.onSuccess(result);
                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }

                            @Override
                            public void onError(RouterErrorResult errorResult) {
                                super.onError(errorResult);
                                if (!emitter.isDisposed()) {
                                    emitter.onError(errorResult.getError());
                                }
                            }
                        }, Activity.RESULT_OK);
            }
        });

    }

}
