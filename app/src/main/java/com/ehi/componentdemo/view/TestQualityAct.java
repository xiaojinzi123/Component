package com.ehi.componentdemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ehi.base.InterceptorConfig;
import com.ehi.base.ModuleConfig;
import com.ehi.base.bean.User;
import com.ehi.base.interceptor.TimeConsumingInterceptor;
import com.ehi.base.view.BaseAct;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiCallback;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.impl.EHiRouterErrorResult;
import com.ehi.component.impl.EHiRouterInterceptor;
import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.impl.EHiRouterResult;
import com.ehi.component.impl.EHiRxRouter;
import com.ehi.component.support.EHiCallbackAdapter;
import com.ehi.component.support.NavigationDisposable;
import com.ehi.component.support.Utils;
import com.ehi.componentdemo.R;
import com.ehi.componentdemo.util.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 测试代码质量的界面
 */
@EHiRouterAnno(
        host = ModuleConfig.App.NAME,
        value = ModuleConfig.App.TEST_QUALITY,
        desc = "测试代码质量的界面"
)
public class TestQualityAct extends BaseAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_quality_act);
    }

    public void cancelImmediately(View view) {
        NavigationDisposable navigationDisposable = EHiRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .query("data", "cancelImmediately")
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        ToastUtil.toastShort("测试失败\n路由成功");
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        ToastUtil.toastLong("测试失败\n路由失败：" + Utils.getRealMessage(errorResult.getError()));
                    }

                    @Override
                    public void onEvent(@Nullable EHiRouterResult result, @Nullable EHiRouterErrorResult errorResult) {
                        ToastUtil.toastLong("测试失败\n路由失败：onEvent方法");
                    }

                    @Override
                    public void onCancel(@NonNull EHiRouterRequest request) {
                        ToastUtil.toastShort("测试成功\n被取消了");
                    }
                });
        navigationDisposable.cancel();
    }

    public void cancelImmediatelyWithGetRx(View view) {
        Disposable disposable = EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .requestCode(123)
                .query("data", "cancelImmediately")
                .call()
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        ToastUtil.toastLong("测试成功");
                    }
                })
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        ToastUtil.toastShort("测试失败,onComplete");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.toastShort("测试失败,onError");
                    }
                });
        disposable.dispose();
    }

    public void cancelImmediatelyWithGetIntent(View view) {

        Disposable disposable = EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .requestCode(123)
                .query("data", "cancelImmediately")
                .intentCall()
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        ToastUtil.toastLong("测试成功");
                    }
                })
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        ToastUtil.toastShort("测试失败,onSuccess");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.toastShort("测试失败,onError");
                    }
                });
        disposable.dispose();

    }

    public void withoutHostOrPath1(View view) {
        EHiRxRouter
                .with(this)
                //.host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .requestCode(123)
                .query("data", "withoutHostOrPath1")
                .intentCall()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        ToastUtil.toastShort("测试失败\n,onSuccess");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.toastShort("测试成功\n,onError：" + Utils.getRealMessage(throwable));
                    }
                });
    }

    public void withoutHostOrPath2(View view) {
        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                //.path(ModuleConfig.Module1.TEST)
                .requestCode(123)
                .query("data", "withoutHostOrPath1")
                .intentCall()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        ToastUtil.toastShort("测试失败\n,onSuccess");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.toastShort("测试成功\n,onError：" + Utils.getRealMessage(throwable));
                    }
                });
    }

    public void useSameRequestCode(View view) {
        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .requestCode(123)
                .interceptors(new EHiRouterInterceptor() {
                    @Override
                    public void intercept(final Chain chain) throws Exception {
                        EHiRxRouter.with(chain.request().getRawContext())
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
                        ToastUtil.toastShort("测试失败\n,onSuccess");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.toastShort("测试成功\n,onError：" + Utils.getRealMessage(throwable));
                    }
                });
    }

    public void useSameRequestCode1(View view) {
        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .requestCode(123)
                .interceptors(new EHiRouterInterceptor() {
                    @Override
                    public void intercept(Chain chain) throws Exception {
                        chain.callback().onError(new RuntimeException("NO passing"));
                    }
                })
                .query("data", "useSameRequestCode1")
                .intentCall()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        ToastUtil.toastShort("测试失败\n,onSuccess");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.toastShort("测试成功\n,onError：" + Utils.getRealMessage(throwable));
                    }
                });
    }

    public void getIntentWithoutRequestCode(View view) {
        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .query("data", "getIntentWithoutRequestCode")
                .intentCall()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        ToastUtil.toastShort("测试失败\n,onSuccess");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.toastShort("测试成功\n,onError：" + Utils.getRealMessage(throwable));
                    }
                });
    }

    public void cancelAuto1(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .query("data", "cancelAuto")
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        ToastUtil.toastShort("测试失败\n路由成功");
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        ToastUtil.toastLong("测试成功\n路由失败：" + Utils.getRealMessage(errorResult.getError()));
                    }

                    @Override
                    public void onCancel(@NonNull EHiRouterRequest request) {
                        ToastUtil.toastShort("测试失败\n自动被取消了");
                    }
                });
        finish();
    }

    public void cancelAuto2(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .interceptors(TimeConsumingInterceptor.class)
                .query("data", "cancelAuto")
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        ToastUtil.toastShort("测试失败\n路由成功");
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        ToastUtil.toastLong("测试失败\n路由失败：" + Utils.getRealMessage(errorResult.getError()));
                    }

                    @Override
                    public void onEvent(@Nullable EHiRouterResult result, @Nullable EHiRouterErrorResult errorResult) {
                    }

                    @Override
                    public void onCancel(@NonNull EHiRouterRequest request) {
                        ToastUtil.toastShort("测试成功\n自动被取消了");
                    }
                });
        finish();
    }

    public void targetNotFound(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.NOT_FOUND_TEST)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        ToastUtil.toastShort("测试失败\n路由成功");
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        ToastUtil.toastLong("测试成功\n路由失败：\n" + Utils.getRealThrowable(errorResult.getError()).getClass().getSimpleName() + ":" + Utils.getRealMessage(errorResult.getError()));
                    }

                    @Override
                    public void onCancel(@NonNull EHiRouterRequest request) {
                        ToastUtil.toastShort("测试失败\n自动被取消了");
                    }
                });
    }

    private NavigationDisposable temp = null;

    public void cancelAfterRouter(View view) {
        temp = EHiRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        ToastUtil.toastShort("测试成功\n路由成功");
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        ToastUtil.toastLong("测试失败\n路由失败：\n" + Utils.getRealThrowable(errorResult.getError()).getClass().getSimpleName() + ":" + Utils.getRealMessage(errorResult.getError()));
                    }

                    @Override
                    public void onCancel(@NonNull EHiRouterRequest request) {
                        ToastUtil.toastShort("测试失败\n自动被取消了");
                    }

                    @Override
                    public void onEvent(@Nullable EHiRouterResult result, @Nullable EHiRouterErrorResult errorResult) {
                        if (temp != null) {
                            temp.cancel();
                        }
                    }
                });
    }

    public void runOnChildThread(View view) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                EHiRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST)
                        .query("data", "cancelAuto")
                        .navigate(new EHiCallbackAdapter() {
                            @Override
                            public void onSuccess(@NonNull EHiRouterResult result) {
                                ToastUtil.toastShort("测试失败\n路由成功");
                            }

                            @Override
                            public void onError(@NonNull EHiRouterErrorResult errorResult) {
                                ToastUtil.toastLong("测试成功\n路由失败：" + Utils.getRealMessage(errorResult.getError()));
                            }

                            @Override
                            public void onCancel(@NonNull EHiRouterRequest request) {
                                ToastUtil.toastShort("测试失败\n自动被取消了");
                            }
                        });
            }
        }.start();
    }

    public void runOnChildThread1(View view) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                EHiRxRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST)
                        .query("data", "cancelAuto")
                        .call()
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                ToastUtil.toastShort("测试失败\n,onSuccess");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ToastUtil.toastShort("测试成功\n,onError：" + Utils.getRealMessage(throwable));
                            }
                        });
            }
        }.start();
    }

}
