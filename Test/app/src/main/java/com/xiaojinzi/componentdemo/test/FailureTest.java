package com.xiaojinzi.componentdemo.test;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.impl.RxRouter;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.componentdemo.util.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 失败测试的测试用例
 */
public class FailureTest implements TestExecutor {

    private TestContext mTestContext;

    private Context mContext;

    @Override
    public Completable execute(TestContext testContext) {
        mTestContext = testContext;
        mContext = testContext.context();
        return Completable.concatArray(
                testContext.wrapTask(withoutHostOrPath1()).doOnComplete(() -> testContext.addTaskPassMsg("withoutHostOrPath1")),
                testContext.wrapTask(withoutHostOrPath2()).doOnComplete(() -> testContext.addTaskPassMsg("withoutHostOrPath2")),
                testContext.wrapTask(useSameRequestCode()).doOnComplete(() -> testContext.addTaskPassMsg("useSameRequestCode")),
                testContext.wrapTask(getIntentWithoutRequestCode()).doOnComplete(() -> testContext.addTaskPassMsg("getIntentWithoutRequestCode")),
                testContext.wrapTask(targetNotFound()).doOnComplete(() -> testContext.addTaskPassMsg("targetNotFound"))
                /*testContext.wrapTask(crashOnAfterJumpAction()).doOnComplete(() -> testContext.addTaskPassMsg("crashOnAfterJumpAction"))*/
        );
    }

    public Completable withoutHostOrPath1() {
        return mTestContext.testWrap(emitter -> RxRouter
                .with(mContext)
                //.host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_AUTORETURN1)
                .requestCode(123)
                .putString("data", "withoutHostOrPath1")
                .intentCall()
                .subscribe(
                        intent -> {
                            emitter.onError(new NavigationFailException("request should be error"));
                        },
                        throwable -> {
                            emitter.onComplete();
                        }
                ));
    }

    public Completable withoutHostOrPath2() {
        return mTestContext.testWrap(emitter -> RxRouter
                .with(mContext)
                .host(ModuleConfig.Module1.NAME)
                //.path(ModuleConfig.Module1.TEST_AUTORETURN)
                .requestCode(123)
                .putString("data", "withoutHostOrPath1")
                .intentCall()
                .subscribe(
                        intent -> {
                            emitter.onError(new NavigationFailException("request should be error"));
                        },
                        throwable -> {
                            emitter.onComplete();
                        }
                ));
    }

    public Completable useSameRequestCode() {

        return mTestContext.testWrap(emitter -> RxRouter
                .with(mContext)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_AUTORETURN1)
                .requestCode(123)
                .interceptors((RouterInterceptor) chain ->
                        RxRouter.with(chain.request().getRawContext())
                                .host(ModuleConfig.User.NAME)
                                .path(ModuleConfig.User.LOGIN)
                                .requestCode(123)
                                .intentCall()
                                .doOnSuccess(intent -> ToastUtil.toastShort("登录成功,1秒后跳转到目标界面"))
                                .observeOn(Schedulers.io())
                                .delay(1, TimeUnit.SECONDS)
                                .map(intent -> (User) intent.getSerializableExtra("data"))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        intent -> {
                                            chain.proceed(chain.request());
                                        },
                                        throwable -> {
                                            chain.callback().onError(new Exception(throwable));
                                        }
                                )
                )
                .query("data", "useSameRequestCode")
                .intentCall()
                .subscribe(
                        intent -> emitter.onError(new NavigationFailException("request should be error")),
                        throwable -> emitter.onComplete()
                )
        );

    }

    public Completable getIntentWithoutRequestCode() {
        return mTestContext.testWrap(emitter -> RxRouter
                .with(mContext)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_AUTORETURN)
                .query("data", "getIntentWithoutRequestCode")
                .intentCall()
                .subscribe(
                        intent -> {
                            emitter.onError(new NavigationFailException("request should be error"));
                        },
                        throwable -> {
                            emitter.onComplete();
                        }
                )
        );
    }

    public Completable targetNotFound() {
        return mTestContext.testWrap(emitter -> Router
                .with(mContext)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.NOT_FOUND_TEST)
                .putString("data", "targetNotFound")
                .forward(new CallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull RouterResult result) {
                        emitter.onError(new NavigationFailException("request should be error"));
                    }

                    @Override
                    public void onError(@NonNull RouterErrorResult errorResult) {
                        emitter.onComplete();
                    }
                }));
    }

}
