package com.xiaojinzi.componentdemo.test;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.component.bean.ActivityResult;
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
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
                testContext.wrapTask(targetNotFound()).doOnComplete(() -> testContext.addTaskPassMsg("targetNotFound")),
                testContext.wrapTask(crashOnAfterJumpAction()).doOnComplete(() -> testContext.addTaskPassMsg("crashOnAfterJumpAction"))
        );
    }

    public Completable withoutHostOrPath1() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                RxRouter
                        .with(mContext)
                        //.host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN1)
                        .requestCode(123)
                        .putString("data", "withoutHostOrPath1")
                        .intentCall()
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                emitter.onError(new NavigationFailException("request should be error"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                emitter.onComplete();
                            }
                        });
            }
        });
    }

    public Completable withoutHostOrPath2() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                RxRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        //.path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "withoutHostOrPath1")
                        .intentCall()
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                emitter.onError(new NavigationFailException("request should be error"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                emitter.onComplete();
                            }
                        });
            }
        });
    }

    public Completable useSameRequestCode() {

        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                RxRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN1)
                        .requestCode(123)
                        .interceptors(new RouterInterceptor() {
                            @Override
                            public void intercept(final Chain chain) throws Exception {
                                RxRouter.with(chain.request().getRawContext())
                                        .host(ModuleConfig.User.NAME)
                                        .path(ModuleConfig.User.LOGIN)
                                        .requestCode(123)
                                        .intentCall()
                                        .doOnSuccess(new Consumer<Intent>() {
                                            @Override
                                            public void accept(Intent intent) throws Exception {
                                                ToastUtil.toastShort("登录成功,1秒后跳转到目标界面");
                                            }
                                        })
                                        .observeOn(Schedulers.io())
                                        .delay(1, TimeUnit.SECONDS)
                                        .map(new Function<Intent, User>() {
                                            @Override
                                            public User apply(Intent intent) throws Exception {
                                                return (User) intent.getSerializableExtra("data");
                                            }
                                        })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<User>() {
                                            @Override
                                            public void accept(User intent) throws Exception {
                                                chain.proceed(chain.request());
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                chain.callback().onError(new Exception(throwable));
                                            }
                                        });
                            }
                        })
                        .query("data", "useSameRequestCode")
                        .intentCall()
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                emitter.onError(new NavigationFailException("request should be error"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                emitter.onComplete();
                            }
                        });

            }
        });

    }

    public Completable getIntentWithoutRequestCode() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                RxRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .query("data", "getIntentWithoutRequestCode")
                        .intentCall()
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                emitter.onError(new NavigationFailException("request should be error"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                emitter.onComplete();
                            }
                        });
            }
        });
    }

    public Completable targetNotFound() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router
                        .with(mContext)
                        .host(ModuleConfig.App.NAME)
                        .path(ModuleConfig.App.NOT_FOUND_TEST)
                        .putString("data", "targetNotFound")
                        .navigate(new CallbackAdapter() {
                            @Override
                            public void onSuccess(@NonNull RouterResult result) {
                                emitter.onError(new NavigationFailException("request should be error"));
                            }

                            @Override
                            public void onError(@NonNull RouterErrorResult errorResult) {
                                emitter.onComplete();
                            }
                        });
            }
        });
    }

    /**
     * 这个会导致界面已经跳过去了,但是回调啥的都没了,因为出现的异常
     */
    public Completable crashOnAfterJumpAction() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                RxRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN1)
                        .requestCode(123)
                        .putString("data", "crashOnAfterJumpAction")
                        .afterJumpAction(new com.xiaojinzi.component.support.Action() {
                            @Override
                            public void run() throws Exception {
                                throw new NullPointerException("test exception on afterJumpAction");
                            }
                        })
                        .activityResultCall()
                        .subscribe(new Consumer<ActivityResult>() {
                            @Override
                            public void accept(ActivityResult eHiActivityResult) throws Exception {
                                emitter.onError(new NavigationFailException("request should be error"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                emitter.onComplete();
                            }
                        });
            }
        });


    }

}
