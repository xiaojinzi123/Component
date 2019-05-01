package com.xiaojinzi.componentdemo.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.interceptor.DialogShowInterceptor;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.impl.RxRouter;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.component.support.NavigationDisposable;
import com.xiaojinzi.component.support.Utils;
import com.xiaojinzi.componentdemo.R;
import com.xiaojinzi.componentdemo.util.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 测试代码质量的界面
 */
@RouterAnno(
        host = ModuleConfig.App.NAME,
        path = ModuleConfig.App.TEST_QUALITY,
        desc = "测试代码质量的界面"
)
public class TestQualityAct extends BaseAct {

    private TextView resultColor;

    private AlertDialog alertDialog;

    private Fragment innerFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_quality_act);
        resultColor = findViewById(R.id.view_result_color);
        innerFragment = new Fragment();
        getSupportFragmentManager().beginTransaction().add(innerFragment, "innerFragment").commit();
        alertDialog = new AlertDialog.Builder(mContext)
                .setMessage("测试 AlertDialog 中使用 Rx 获取目标界面数据功能")
                .create();
    }

    private void testFailure() {
        testFailure(null);
    }

    private void testFailure(@Nullable Throwable throwable) {
        resultColor.setBackgroundColor(Color.RED);
        if (throwable != null) {
            resultColor.setText(Utils.getRealThrowable(throwable).getMessage());
        }
    }

    private void testSuccess() {
        resultColor.setBackgroundColor(Color.GREEN);
        resultColor.setText("恭喜所有的用例都测试通过了,可以去发布新的版本啦");
    }

    private void testSuccess1() {
        resultColor.setBackgroundColor(Color.GREEN);
        resultColor.setText("用例都测试通过了,接着去测试其他情况吧");
    }

    private void addTaskPassMsg(String taskName) {

        resultColor.setText(taskName + " 任务测试通过" + "\n" + resultColor.getText().toString());

    }

    private Completable wrapTask(Completable completable) {
        return completable
                .observeOn(Schedulers.io())
                .delay(1200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Completable allCancel() {
        return Completable.concatArray(
                wrapTask(cancelImmediately()).doOnComplete(() -> addTaskPassMsg("cancelImmediately")),
                wrapTask(cancelImmediatelyWithGetRx()).doOnComplete(() -> addTaskPassMsg("cancelImmediatelyWithGetRx")),
                wrapTask(cancelImmediatelyWithGetIntent()).doOnComplete(() -> addTaskPassMsg("cancelImmediatelyWithGetIntent")),
                wrapTask(cancelFromActivityWhenActivityFinish()).doOnComplete(() -> addTaskPassMsg("cancelFromActivityWhenActivityFinish")),
                wrapTask(cancelFromFragmentWhenActivityFinish()).doOnComplete(() -> addTaskPassMsg("cancelFromFragmentWhenActivityFinish"))
        );
    }

    private Completable allFailure() {
        return Completable.concatArray(
                wrapTask(withoutHostOrPath1()).doOnComplete(() -> addTaskPassMsg("withoutHostOrPath1")),
                wrapTask(withoutHostOrPath2()).doOnComplete(() -> addTaskPassMsg("withoutHostOrPath2")),
                wrapTask(useSameRequestCode()).doOnComplete(() -> addTaskPassMsg("useSameRequestCode")),
                wrapTask(getIntentWithoutRequestCode()).doOnComplete(() -> addTaskPassMsg("getIntentWithoutRequestCode")),
                wrapTask(targetNotFound()).doOnComplete(() -> addTaskPassMsg("targetNotFound")),
                wrapTask(crashOnAfterJumpAction()).doOnComplete(() -> addTaskPassMsg("crashOnAfterJumpAction"))
        );
    }

    private Completable allSuccess() {
        return Completable.concatArray(
                wrapTask(runOnChildThread()).doOnComplete(() -> addTaskPassMsg("runOnChildThread")),
                wrapTask(runOnChildThread1()).doOnComplete(() -> addTaskPassMsg("runOnChildThread1")),
                wrapTask(rxGetIntent()).doOnComplete(() -> addTaskPassMsg("rxGetIntent")),
                wrapTask(rxGetIntentInDialog()).doOnComplete(() -> addTaskPassMsg("rxGetIntentInDialog")),
                wrapTask(rxGetIntentUseFragment()).doOnComplete(() -> addTaskPassMsg("rxGetIntentUseFragment")),
                wrapTask(goToNeedLoginView()).doOnComplete(() -> addTaskPassMsg("goToNeedLoginView")),
                wrapTask(rxGoToNeedLoginView()).doOnComplete(() -> addTaskPassMsg("rxGoToNeedLoginView")),
                wrapTask(goToViewWithDialog()).doOnComplete(() -> addTaskPassMsg("goToViewWithDialog")),
                wrapTask(testNormalJump()).doOnComplete(() -> addTaskPassMsg("testNormalJump")),
                wrapTask(testNormalJumpUseFragment()).doOnComplete(() -> addTaskPassMsg("testNormalJumpUseFragment")),
                wrapTask(testOpenurl()).doOnComplete(() -> addTaskPassMsg("testOpenurl")),
                wrapTask(testPassQuery()).doOnComplete(() -> addTaskPassMsg("testPassQuery")),
                wrapTask(testModifyByInterceptor()).doOnComplete(() -> addTaskPassMsg("testModifyByInterceptor")),
                wrapTask(testParameterByte()).doOnComplete(() -> addTaskPassMsg("testParameterByte")),
                wrapTask(testParameterShort()).doOnComplete(() -> addTaskPassMsg("testParameterShort")),
                wrapTask(testParameterInt()).doOnComplete(() -> addTaskPassMsg("testParameterInt")),
                wrapTask(testParameterLong()).doOnComplete(() -> addTaskPassMsg("testParameterLong")),
                wrapTask(testParameterFloat()).doOnComplete(() -> addTaskPassMsg("testParameterFloat")),
                wrapTask(testParameterDouble()).doOnComplete(() -> addTaskPassMsg("testParameterDouble")),
                wrapTask(testParameterBoolean()).doOnComplete(() -> addTaskPassMsg("testParameterBoolean")),
                wrapTask(testParameterString()).doOnComplete(() -> addTaskPassMsg("testParameterString")),
                wrapTask(testFieldInject()).doOnComplete(() -> addTaskPassMsg("testFieldInject"))
        );
    }

    public void testAllCancel(View v) {

        compositeDisposable.clear();

        Completable observable = Completable.concatArray(
                wrapTask(allCancel()).doOnComplete(() -> addTaskPassMsg("allCancel"))
        );

        compositeDisposable.add(observable
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        testFailure();
                        resultColor.setText("");
                    }
                })
                .subscribe(
                        () -> testSuccess1(),
                        throwable -> {
                            testFailure(throwable);
                        }
                )
        );
    }

    public void testAllFailure(View v) {

        compositeDisposable.clear();

        Completable observable = Completable.concatArray(
                wrapTask(allFailure()).doOnComplete(() -> addTaskPassMsg("allFailure"))
        );

        compositeDisposable.add(observable
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        testFailure();
                        resultColor.setText("");
                    }
                })
                .subscribe(
                        () -> testSuccess1(),
                        throwable -> {
                            testFailure(throwable);
                        }
                )
        );
    }

    public void testAllSuccess(View v) {

        compositeDisposable.clear();
        Completable observable = Completable.concatArray(
                wrapTask(allSuccess()).doOnComplete(() -> addTaskPassMsg("allSuccess"))
        );
        compositeDisposable.add(observable
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        testFailure();
                        resultColor.setText("");
                    }
                })
                .subscribe(
                        () -> testSuccess1(),
                        throwable -> {
                            testFailure(throwable);
                        }
                )
        );
    }

    public void testAll(View view) {

        compositeDisposable.clear();

        Completable observable = Completable.concatArray(
                wrapTask(allCancel()).doOnComplete(() -> addTaskPassMsg("allCancel")),
                wrapTask(allFailure()).doOnComplete(() -> addTaskPassMsg("allFailure")),
                wrapTask(allSuccess()).doOnComplete(() -> addTaskPassMsg("allSuccess"))
        );

        observable
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        testFailure();
                        resultColor.setText("");
                    }
                })
                .subscribe(
                        () -> testSuccess(),
                        throwable -> {
                            testFailure(throwable);
                        }
                );

    }

    // -------------------------------------------------------- 失败的例子 -------------------------------start

    public Completable cancelImmediately() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }
                NavigationDisposable navigationDisposable = Router
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .query("data", "cancelImmediately")
                        .navigate(new CallbackAdapter() {
                            @Override
                            public void onSuccess(@NonNull RouterResult result) {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onError(new NavigationFailException("request should be canceled"));
                            }

                            @Override
                            public void onError(@NonNull RouterErrorResult errorResult) {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onError(errorResult.getError());
                            }

                            @Override
                            public void onCancel(@NonNull RouterRequest request) {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onComplete();
                            }
                        });
                navigationDisposable.cancel();
            }
        });
    }

    public Completable cancelImmediatelyWithGetRx() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                RxRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "cancelImmediately")
                        .call()
                        .doOnDispose(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onComplete();
                            }
                        })
                        .doOnSubscribe(disposable -> disposable.dispose())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onError(new NavigationFailException("request should be cancelde"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onError(new NavigationFailException("request should be cancelde", throwable));
                            }
                        });

            }
        });

    }

    public Completable cancelImmediatelyWithGetIntent() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                Disposable disposable = RxRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "cancelImmediately")
                        .intentCall()
                        .doOnDispose(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }
                        })
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onError(new NavigationFailException("request should be canceled"));
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onError(new NavigationFailException("request should be canceled", throwable));
                                }
                            }
                        });
                disposable.dispose();

            }
        });

    }

    public Completable cancelFromActivityWhenActivityFinish() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                RxRouter.with(mContext)
                        .host(ModuleConfig.Help.NAME)
                        .path(ModuleConfig.Help.CANCEL_FOR_TEST)
                        .requestCodeRandom()
                        .resultCodeMatchCall(RESULT_OK)
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onError(throwable);
                                }
                            }
                        });

            }
        });

    }

    public Completable cancelFromFragmentWhenActivityFinish() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                RxRouter.with(mContext)
                        .host(ModuleConfig.Help.NAME)
                        .path(ModuleConfig.Help.CANCEL_FOR_TEST)
                        .requestCodeRandom()
                        .putBoolean("isUseFragment", true)
                        .resultCodeMatchCall(RESULT_OK)
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onError(throwable);
                                }
                            }
                        });

            }
        });

    }

    public Completable withoutHostOrPath1() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }
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
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onError(new NavigationFailException("request should be error"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onComplete();
                            }
                        });
            }
        });

    }

    public Completable withoutHostOrPath2() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }
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
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onError(new NavigationFailException("request should be error"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onComplete();
                            }
                        });
            }
        });

    }

    public Completable useSameRequestCode() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

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
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onError(new NavigationFailException("request should be error"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onComplete();
                            }
                        });

            }
        });
    }

    public Completable getIntentWithoutRequestCode() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }
                RxRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .query("data", "getIntentWithoutRequestCode")
                        .intentCall()
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onError(new NavigationFailException("request should be error"));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onComplete();
                            }
                        });
            }
        });
    }

    public Completable targetNotFound() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }
                Router
                        .with(mContext)
                        .host(ModuleConfig.App.NAME)
                        .path(ModuleConfig.App.NOT_FOUND_TEST)
                        .putString("data", "targetNotFound")
                        .navigate(new CallbackAdapter() {
                            @Override
                            public void onSuccess(@NonNull RouterResult result) {
                                if (emitter.isDisposed()) {
                                    return;
                                }
                                emitter.onError(new NavigationFailException("request should be error"));
                            }

                            @Override
                            public void onError(@NonNull RouterErrorResult errorResult) {
                                if (emitter.isDisposed()) {
                                    return;
                                }
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

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }
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
                                if (!emitter.isDisposed()) {
                                    emitter.onError(new NavigationFailException("request should be error"));
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }
                        });
            }
        });


    }

    // -------------------------------------------------------- 失败的例子 -------------------------------end

    // -------------------------------------------------------- 成功的例子 -------------------------------start

    public Completable runOnChildThread() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                new Thread("child thread") {
                    @Override
                    public void run() {
                        super.run();
                        Router
                                .with(mContext)
                                .host(ModuleConfig.Module1.NAME)
                                .path(ModuleConfig.Module1.TEST_AUTORETURN1)
                                .putString("data", "runOnChildThread")
                                .navigate(new CallbackAdapter() {
                                    @Override
                                    public void onSuccess(@NonNull RouterResult result) {
                                        if (!emitter.isDisposed()) {
                                            emitter.onComplete();
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull RouterErrorResult errorResult) {
                                        if (!emitter.isDisposed()) {
                                            emitter.onError(errorResult.getError());
                                        }
                                    }

                                    @Override
                                    public void onCancel(@NonNull RouterRequest request) {
                                        if (!emitter.isDisposed()) {
                                            emitter.onError(new NavigationFailException("request be canceled"));
                                        }
                                    }
                                });
                    }
                }.start();

            }
        });


    }

    public Completable runOnChildThread1() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }
                new Thread("child thread") {
                    @Override
                    public void run() {
                        super.run();
                        RxRouter
                                .with(mContext)
                                .host(ModuleConfig.Module1.NAME)
                                .path(ModuleConfig.Module1.TEST_AUTORETURN)
                                .query("data", "runOnChildThread1")
                                .call()
                                .subscribe(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        if (!emitter.isDisposed()) {
                                            emitter.onComplete();
                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        if (!emitter.isDisposed()) {
                                            emitter.onError(throwable);
                                        }
                                    }
                                });
                    }
                }.start();

            }
        });


    }

    public Completable rxGetIntent() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                RxRouter.with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "rxGetIntent")
                        .intentCall()
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onError(new NavigationFailException("request shourld not be error", throwable));
                                }
                            }
                        });

            }
        });

    }

    public Completable rxGetIntentInDialog() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                RxRouter.with(alertDialog.getContext())
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "rxGetIntentInDialog")
                        .intentCall()
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                Thread.sleep(2000);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                alertDialog.show();
                            }
                        })
                        .doOnEvent(new BiConsumer<Intent, Throwable>() {
                            @Override
                            public void accept(Intent intent, Throwable throwable) throws Exception {
                                alertDialog.dismiss();
                            }
                        })
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onError(new NavigationFailException("request shourld not be error", throwable));
                                }
                            }
                        });

            }
        });

    }

    public Completable rxGetIntentUseFragment() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                RxRouter.with(innerFragment)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .requestCode(123)
                        .putString("data", "rxGetIntentUseFragment")
                        .intentCall()
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onError(new NavigationFailException("request shourld not be error", throwable));
                                }
                            }
                        });

            }
        });

    }

    public Completable goToNeedLoginView() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                Router.with(mContext)
                        .host(ModuleConfig.User.NAME)
                        .path(ModuleConfig.User.PERSON_CENTER_FOR_TEST)
                        .navigate(new CallbackAdapter() {
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
                                    emitter.onError(new NavigationFailException("request shourld not be error", errorResult.getError()));
                                }
                            }
                        });

            }
        });

    }

    public Completable rxGoToNeedLoginView() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                RxRouter.with(mContext)
                        .host(ModuleConfig.User.NAME)
                        .path(ModuleConfig.User.PERSON_CENTER_FOR_TEST)
                        .call()
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!emitter.isDisposed()) {
                                    emitter.onError(new NavigationFailException("request shourld not be error", throwable));
                                }
                            }
                        });

            }
        });

    }

    public Completable goToViewWithDialog() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }

                Router.with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST_AUTORETURN)
                        .putString("data", "goToViewWithDialog")
                        .interceptors(DialogShowInterceptor.class)
                        .navigate(new CallbackAdapter() {
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
                                    emitter.onError(new NavigationFailException("request should not be error"));
                                }
                            }
                        });

            }
        });

    }

    private Completable testNormalJump() {
        return Completable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            Router
                    .with(mContext)
                    .host(ModuleConfig.Module1.NAME)
                    .path(ModuleConfig.Module1.TEST_AUTORETURN)
                    .putString("data", "testNormalJump")
                    .navigate(new CallbackAdapter() {
                        @Override
                        public void onSuccess(@NonNull RouterResult result) {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onComplete();
                        }

                        @Override
                        public void onError(@NonNull RouterErrorResult errorResult) {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onError(errorResult.getError());
                        }

                        @Override
                        public void onCancel(@NonNull RouterRequest request) {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onError(new NavigationFailException("request is canceled"));
                        }
                    });

        });
    }

    private Completable testNormalJumpUseFragment() {
        return Completable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            Router
                    .with(innerFragment)
                    .host(ModuleConfig.Module1.NAME)
                    .path(ModuleConfig.Module1.TEST_AUTORETURN1)
                    .putString("data", "testNormalJump")
                    .navigate(new CallbackAdapter() {
                        @Override
                        public void onSuccess(@NonNull RouterResult result) {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onComplete();
                        }

                        @Override
                        public void onError(@NonNull RouterErrorResult errorResult) {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onError(errorResult.getError());
                        }

                        @Override
                        public void onCancel(@NonNull RouterRequest request) {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onError(new NavigationFailException("request is canceled"));
                        }
                    });

        });
    }

    private Completable testOpenurl() {
        return Completable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }

            final String url = new RouterRequest.URIBuilder()
                    .host(ModuleConfig.Module1.NAME)
                    .path(ModuleConfig.Module1.TEST_QUERY)
                    .query("name", "openUrlName")
                    .query("pass", "openUrlPassword")
                    .buildURL();

            RxRouter
                    .with(mContext)
                    .host(ModuleConfig.Module1.NAME)
                    .path(ModuleConfig.Module1.TEST_QUERY)
                    .url(url)
                    .putBoolean("isReturnAuto", true)
                    .requestCodeRandom()
                    .resultCodeMatchCall(RESULT_OK)
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onComplete();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onError(throwable);
                        }
                    });

        });
    }

    private Completable testPassQuery() {
        return Completable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            RxRouter
                    .with(mContext)
                    .host(ModuleConfig.Module1.NAME)
                    .path(ModuleConfig.Module1.TEST_QUERY)
                    .query("name", "testName")
                    .query("pass", "testPass")
                    .query("isReturnAuto", true)
                    .requestCodeRandom()
                    .resultCodeMatchCall(RESULT_OK)
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onComplete();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onError(throwable);
                        }
                    });

        });
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

                        android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(chain.request().getRawActivity())
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
                .resultCodeMatchCall(RESULT_OK);
    }

    private Completable testParameterByte() {
        Completable completable1 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_BYTE)
                .putByte("value", (byte) 1)
                .putByte("valueDefault", (byte) 1)
                .call();
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_BYTE)
                .call();

        Completable completable3 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_BYTE_BOX)
                .putByte("value", (byte) 1)
                .putByte("valueDefault", (byte) 1)
                .call();

        Completable completable4 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_BYTE_BOX)
                .call();

        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2), wrapTask(completable3), wrapTask(completable4));

    }

    private Completable testParameterShort() {
        Completable completable1 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_SHORT)
                .putShort("value", (short) 1)
                .putShort("valueDefault", (short) 1)
                .call();
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_SHORT)
                .call();
        Completable completable3 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_SHORT_BOX)
                .putShort("value", (short) 1)
                .putShort("valueDefault", (short) 1)
                .call();
        Completable completable4 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_SHORT_BOX)
                .call();
        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2), wrapTask(completable3), wrapTask(completable4));
    }

    private Completable testParameterInt() {
        Completable completable1 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_INT)
                .putInt("value", 1)
                .putInt("valueDefault", 1)
                .call();
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_INT)
                .call();
        Completable completable3 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_INT_BOX)
                .putInt("value", 1)
                .putInt("valueDefault", 1)
                .call();
        Completable completable4 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_INT_BOX)
                .call();
        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2), wrapTask(completable3), wrapTask(completable4));
    }

    private Completable testParameterLong() {
        Completable completable1 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_LONG)
                .putLong("value", 1)
                .putLong("valueDefault", 1)
                .call();
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_LONG)
                .call();
        Completable completable3 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_LONG_BOX)
                .putLong("value", 1)
                .putLong("valueDefault", 1)
                .call();
        Completable completable4 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_LONG_BOX)
                .call();
        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2), wrapTask(completable3), wrapTask(completable4));
    }

    private Completable testParameterFloat() {
        Completable completable1 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_FLOAT)
                .putFloat("value", 1f)
                .putFloat("valueDefault", 1f)
                .call();
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_FLOAT)
                .call();
        Completable completable3 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_FLOAT_BOX)
                .putFloat("value", 1f)
                .putFloat("valueDefault", 1f)
                .call();
        Completable completable4 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_FLOAT_BOX)
                .call();
        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2), wrapTask(completable3), wrapTask(completable4));
    }

    private Completable testParameterDouble() {
        Completable completable1 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_DOUBLE)
                .putDouble("value", 1)
                .putDouble("valueDefault", 1)
                .call();
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_DOUBLE)
                .call();
        Completable completable3 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_DOUBLE_BOX)
                .putDouble("value", 1)
                .putDouble("valueDefault", 1)
                .call();
        Completable completable4 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_DOUBLE_BOX)
                .call();
        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2), wrapTask(completable3), wrapTask(completable4));
    }

    private Completable testParameterBoolean() {
        Completable completable1 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_BOOLEAN)
                .putBoolean("value", true)
                .putBoolean("valueDefault", false)
                .call();
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_BOOLEAN)
                .call();
        Completable completable3 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_BOOLEAN_BOX)
                .putBoolean("value", true)
                .putBoolean("valueDefault", false)
                .call();
        Completable completable4 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_BOOLEAN_BOX)
                .call();
        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2), wrapTask(completable3), wrapTask(completable4));
    }

    private Completable testParameterString() {
        Completable completable1 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_STRING)
                .putString("value", "testString")
                .putString("valueDefault", "testString")
                .call();
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_STRING)
                .call();
        Completable completable3 = RxRouter
                .with(mContext)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.TEST_PARAMETER_STRING)
                .putString("value", null)
                .putString("valueDefault", null)
                .call();
        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2), wrapTask(completable3));
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
                .resultCodeMatchCall(RESULT_OK);

        // 测试不传的时候的默认值是否能正常工作
        Completable completable2 = RxRouter
                .with(mContext)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_INJECT1)
                .requestCodeRandom()
                .putBoolean("isReturnAuto", true)
                .resultCodeMatchCall(RESULT_OK);

        return Completable.concatArray(wrapTask(completable1), wrapTask(completable2));

    }

    // -------------------------------------------------------- 成功的例子 -------------------------------end

}
