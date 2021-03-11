package com.xiaojinzi.componentdemo.test;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.impl.RxRouter;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.componentdemo.view.TestQualityAct;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 这个类测试的都是平常的使用,必须全都是正常跳转的
 * note: 只要 拿 activityResult 和 普通跳转在各个线程和 Fragment 和 Dialog 里面是正常,其实就可以了
 * 现在暂时少了 Rx 库的测试
 */
public class SuccessTest implements TestExecutor {

    private TestContext mTestContext;

    private Context mContext;

    Completable wrapTask(Completable completable) {
        return mTestContext.wrapTask(completable);
    }

    @Override
    public Completable execute(TestContext testContext) {
        mTestContext = testContext;
        mContext = testContext.context();

        return Completable.concatArray(
                testContext.wrapTask(testNavigate()).doOnComplete(() -> testContext.addTaskPassMsg("testNavigate")),
                testContext.wrapTask(testNavigatex()).doOnComplete(() -> testContext.addTaskPassMsg("testNavigatex")),
                testContext.wrapTask(testNavigatexx()).doOnComplete(() -> testContext.addTaskPassMsg("testNavigatexx")),
                testContext.wrapTask(rxTestNavigate()).doOnComplete(() -> testContext.addTaskPassMsg("rxTestNavigate")),
                testContext.wrapTask(rxTestNavigatex()).doOnComplete(() -> testContext.addTaskPassMsg("rxTestNavigatex")),
                testContext.wrapTask(testNavigateWithChildThread()).doOnComplete(() -> testContext.addTaskPassMsg("testNavigateWithChildThread")),
                testContext.wrapTask(testNavigateWithChildThreadx()).doOnComplete(() -> testContext.addTaskPassMsg("testNavigateWithChildThreadx")),
                testContext.wrapTask(testNavigateWithChildThreadxx()).doOnComplete(() -> testContext.addTaskPassMsg("testNavigateWithChildThreadxx")),
                testContext.wrapTask(testGetActivityResult()).doOnComplete(() -> testContext.addTaskPassMsg("testGetActivityResult")),
                testContext.wrapTask(testGetActivityResultx()).doOnComplete(() -> testContext.addTaskPassMsg("testGetActivityResultx")),
                testContext.wrapTask(testGetActivityResultxx()).doOnComplete(() -> testContext.addTaskPassMsg("testGetActivityResultxx")),
                testContext.wrapTask(testGetActivityResultWithChildThread()).doOnComplete(() -> testContext.addTaskPassMsg("testGetActivityResultWithChildThread")),
                testContext.wrapTask(testGetActivityResultWithChildThreadx()).doOnComplete(() -> testContext.addTaskPassMsg("testGetActivityResultWithChildThreadx")),
                testContext.wrapTask(testGetActivityResultWithChildThreadxx()).doOnComplete(() -> testContext.addTaskPassMsg("testGetActivityResultWithChildThreadxx")),
                testContext.wrapTask(testGetIntent()).doOnComplete(() -> testContext.addTaskPassMsg("testGetIntent")),
                testContext.wrapTask(testGetIntent1()).doOnComplete(() -> testContext.addTaskPassMsg("testGetIntent1")),
                testContext.wrapTask(testGetResultCode()).doOnComplete(() -> testContext.addTaskPassMsg("testGetResultCode")),
                testContext.wrapTask(testResultCodeMatch()).doOnComplete(() -> testContext.addTaskPassMsg("testResultCodeMatch")),
                testContext.wrapTask(testOpenurl()).doOnComplete(() -> testContext.addTaskPassMsg("testOpenurl")),
                testContext.wrapTask(testPassQuery()).doOnComplete(() -> testContext.addTaskPassMsg("testPassQuery")),
                testContext.wrapTask(testModifyByInterceptor()).doOnComplete(() -> testContext.addTaskPassMsg("testModifyByInterceptor")),
                testContext.wrapTask(testFieldInject()).doOnComplete(() -> testContext.addTaskPassMsg("testFieldInject")),
                testContext.wrapTask(testPutQueryWithUrl()).doOnComplete(() -> testContext.addTaskPassMsg("testPutQueryWithUrl")),
                testContext.wrapTask(goToNeedLoginView()).doOnComplete(() -> testContext.addTaskPassMsg("goToNeedLoginView"))
        );

    }

    /**
     * 基本跳转
     *
     * @return
     */
    public Completable testNavigate() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .putString("data", "testNavigate")
                        .forward(new TestContext.CallbackSuccessIsSuccessful(emitter));
            }
        });

    }

    /**
     * 使用 Fragment 跳转
     *
     * @return
     */
    public Completable testNavigatex() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.fragment())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .putString("data", "testNavigatex")
                        .forward(new TestQualityAct.CallbackSuccessIsSuccessful(emitter));
            }
        });

    }

    /**
     * 使用 Fragment 跳转
     *
     * @return
     */
    public Completable testNavigatexx() {
        return mTestContext.testWrap(emitter ->
                Router.with(mTestContext.dialog().getContext())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .putString("data", "testNavigatexx")
                        .forward(new TestQualityAct.CallbackSuccessIsSuccessful(emitter))
        );

    }

    public Completable rxTestNavigate() {
        return mTestContext.testWrap(emitter -> RxRouter.with(mTestContext.context())
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_AUTORETURN)
                .putString("data", "rxTestNavigate")
                .call()
                .subscribe(
                        () -> emitter.onComplete(),
                        throwable -> emitter.onError(throwable)
                )
        );
    }

    public Completable rxTestNavigatex() {
        return mTestContext.testWrap(emitter -> RxRouter.with(mTestContext.fragment())
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_AUTORETURN)
                .putString("data", "rxTestNavigatex")
                .call()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> emitter.onComplete(),
                        throwable -> emitter.onError(throwable)
                )
        );
    }

    /**
     * 在子线程跑
     *
     * @return
     */
    public Completable testNavigateWithChildThread() {
        return mTestContext.testWrapWithChildThread(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .putString("data", "testNavigateWithChildThread")
                        .forward(new TestQualityAct.CallbackSuccessIsSuccessful(emitter));
            }
        });

    }

    /**
     * 在子线程用Fragment跑
     *
     * @return
     */
    public Completable testNavigateWithChildThreadx() {
        return mTestContext.testWrapWithChildThread(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.fragment())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .putString("data", "testNavigateWithChildThreadx")
                        .forward(new TestQualityAct.CallbackSuccessIsSuccessful(emitter));
            }
        });

    }

    /**
     * 在子线程用Fragment跑
     *
     * @return
     */
    public Completable testNavigateWithChildThreadxx() {
        return mTestContext.testWrapWithChildThread(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.dialog().getContext())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .putString("data", "testNavigateWithChildThreadxx")
                        .forward(new TestQualityAct.CallbackSuccessIsSuccessful(emitter));
            }
        });

    }

    /**
     * 测试拿 Activity Result
     *
     * @return
     */
    public Completable testGetActivityResult() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testGetActivityResult")
                        .forwardForResult(new TestQualityAct.BiCallbackSuccessIsSuccessful(emitter));
            }
        });
    }

    /**
     * Fragment 测试拿 Activity Result
     *
     * @return
     */
    public Completable testGetActivityResultx() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.fragment())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testGetActivityResultx")
                        .forwardForResult(new TestQualityAct.BiCallbackSuccessIsSuccessful(emitter));
            }
        });
    }

    /**
     * Dailog 测试拿 Activity Result
     *
     * @return
     */
    public Completable testGetActivityResultxx() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.dialog().getContext())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testGetActivityResultxx")
                        .forwardForResult(new TestQualityAct.BiCallbackSuccessIsSuccessful(emitter));
            }
        });
    }

    /**
     * 子线程中拿 Activity Result
     *
     * @return
     */
    public Completable testGetActivityResultWithChildThread() {
        return mTestContext.testWrapWithChildThread(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testGetActivityResultWithChildThread")
                        .forwardForResult(new TestQualityAct.BiCallbackSuccessIsSuccessful(emitter));
            }
        });
    }

    /**
     * 子线程中用fragment拿 Activity Result
     *
     * @return
     */
    public Completable testGetActivityResultWithChildThreadx() {
        return mTestContext.testWrapWithChildThread(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.fragment())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testGetActivityResultWithChildThreadx")
                        .forwardForResult(new TestQualityAct.BiCallbackSuccessIsSuccessful(emitter));
            }
        });
    }

    /**
     * 子线程中用 dialog 拿 Activity Result
     *
     * @return
     */
    public Completable testGetActivityResultWithChildThreadxx() {
        return mTestContext.testWrapWithChildThread(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.dialog().getContext())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testGetActivityResultWithChildThreadxx")
                        .forwardForResult(new TestQualityAct.BiCallbackSuccessIsSuccessful(emitter));
            }
        });
    }

    /**
     * 测试拿 Intent
     *
     * @return
     */
    public Completable testGetIntent() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testGetIntent")
                        .forwardForIntent(new TestQualityAct.BiCallbackSuccessIsSuccessful(emitter));
            }
        });
    }

    // 下面开始可以不用测试子线程了

    /**
     * 测试拿 Intent 并且匹配 resultCode
     *
     * @return
     */
    public Completable testGetIntent1() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testGetIntent1")
                        .forwardForIntentAndResultCodeMatch(new TestQualityAct.BiCallbackSuccessIsSuccessful(emitter), Activity.RESULT_OK);
            }
        });
    }

    public Completable testGetResultCode() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testGetResultCode")
                        .forwardForResultCode(new TestQualityAct.BiCallbackSuccessIsSuccessful(emitter));
            }
        });
    }

    public Completable testResultCodeMatch() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "testResultCodeMatch")
                        .forwardForResultCodeMatch(new TestQualityAct.CallbackSuccessIsSuccessful(emitter), Activity.RESULT_OK);
            }
        });
    }

    private Completable testOpenurl() {
        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                final String url = new RouterRequest.URIBuilder()
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_QUERY)
                        .query("name", "openUrlName")
                        .query("pass", "openUrlPassword")
                        .buildURL();

                RxRouter
                        .with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_QUERY)
                        .url(url)
                        .putBoolean("isReturnAuto", true)
                        .requestCodeRandom()
                        .resultCodeMatchCall(Activity.RESULT_OK)
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                emitter.onComplete();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                emitter.onError(throwable);
                            }
                        });
            }
        });
    }

    private Completable testPassQuery() {
        return mTestContext.testWrap(
                emitter -> RxRouter
                        .with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_QUERY)
                        .query("name", "testName")
                        .query("pass", "testPass")
                        .query("isReturnAuto", true)
                        .requestCodeRandom()
                        .resultCodeMatchCall(Activity.RESULT_OK)
                        .subscribe(
                                () -> emitter.onComplete(),
                                throwable -> emitter.onError(throwable)
                        )
        );
    }

    private Completable testPassQuery1() {
        return mTestContext.testWrap(
                emitter -> RxRouter
                        .with(mTestContext.context())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_QUERY)
                        .query("name", "testName")
                        .query("pass", "testPass")
                        .query("isReturnAuto", true)
                        .requestCodeRandom()
                        .resultCodeMatchCall(Activity.RESULT_OK)
                        .subscribe(
                                () -> emitter.onComplete(),
                                throwable -> emitter.onError(throwable)
                        )
        );
    }

    private Completable testModifyByInterceptor() {
        return RxRouter
                .with(mContext)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_QUERY)
                .query("name", "testName")
                .query("pass", "testPass")
                .query("expectName", "我是被拦截器修改的 testName")
                .query("isReturnAuto", true)
                .interceptors(new RouterInterceptor() {
                    @Override
                    public void intercept(RouterInterceptor.Chain chain) throws Exception {

                        android.support.v7.app.AlertDialog dialog =
                                new android.support.v7.app.AlertDialog.Builder(chain.request().getRawOrTopActivity())
                                        .setMessage("如果您点击确定,传递过去的名称 'testName' 会被修改为 '我是被拦截器修改的 testName'")
                                        .setPositiveButton("确定", (dialog12, which) -> {
                                        })
                                        .setNegativeButton("取消", (dialog1, which) -> {
                                        })
                                        .create();
                        dialog.show();

                        Completable.complete()
                                .observeOn(Schedulers.io())
                                .delay(2, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        dialog.dismiss();
                                        chain.proceed(chain.request().toBuilder().query("name", "我是被拦截器修改的 testName").build());
                                    }
                                });

                    }
                })
                .requestCodeRandom()
                .resultCodeMatchCall(Activity.RESULT_OK);
    }

    private Completable testFieldInject() {

        Completable completable1 = RxRouter
                .with(mContext)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_INJECT1)
                .requestCodeRandom()
                .putBoolean("isReturnAuto", true)
                .putString("valueString", "valueString")
                .putString("valueStringDefault", "valueStringDefault")
                .putByte("valueByte", (byte) 1)
                .putByte("valueByteDefalut", (byte) 1)
                .putByte("valueByteBox", (byte) 1)
                .putByte("valueByteBoxDefalut", (byte) 1)
                .putShort("valueShort", (short) 1)
                .putShort("valueShortDefalut", (short) 1)
                .putShort("valueShortBox", (short) 1)
                .putShort("valueShortBoxDefalut", (short) 1)
                .putInt("valueInt", 1)
                .putInt("valueIntDefalut", 1)
                .putInt("valueIntBox", 1)
                .putInt("valueIntBoxDefalut", 1)
                .putLong("valueLong", 1)
                .putLong("valueLongDefalut", 1)
                .putLong("valueLongBox", 1)
                .putLong("valueLongBoxDefalut", 1)
                .putBoolean("valueBoolean", true)
                .putBoolean("valueBooleanDefalut", false)
                .putBoolean("valueBooleanBox", true)
                .putBoolean("valueBooleanBoxDefalut", false)
                .resultCodeMatchCall(Activity.RESULT_OK);

        // 测试不传的时候的默认值是否能正常工作
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_INJECT1)
                .requestCodeRandom()
                .putBoolean("isReturnAuto", true)
                .resultCodeMatchCall(Activity.RESULT_OK);

        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2));

    }

    private Completable testPutQueryWithUrl() {
        return RxRouter.with(mContext)
                .url("router://component1/testPutQueryWithUrl?nameFromUrl=hhasjfjdsf")
                .query("nameFromPutQuery", "ewrfgrdsrdff")
                .requestCodeRandom()
                .resultCodeMatchCall(Activity.RESULT_OK);
    }

    public Completable goToNeedLoginView() {

        return mTestContext.testWrap(new TestContext.TestBack() {
            @Override
            public void run(CompletableEmitter emitter) {
                Router.with(mContext)
                        .host(ModuleConfig.User.NAME)
                        .path(ModuleConfig.User.PERSON_CENTER_FOR_TEST)
                        .forward(new CallbackAdapter() {
                            @Override
                            public void onSuccess(@NonNull RouterResult result) {
                                super.onSuccess(result);
                                emitter.onComplete();
                            }

                            @Override
                            public void onError(RouterErrorResult errorResult) {
                                super.onError(errorResult);
                                emitter.onError(new NavigationFailException("request shourld not be error", errorResult.getError()));
                            }
                        });
            }
        });

    }

}
