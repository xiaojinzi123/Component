package com.xiaojinzi.componentdemo.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RxRouter;
import com.xiaojinzi.component.support.Utils;
import com.xiaojinzi.componentdemo.R;
import com.xiaojinzi.componentdemo.test.CancelTest;
import com.xiaojinzi.componentdemo.test.FailureTest;
import com.xiaojinzi.componentdemo.test.SuccessTest;
import com.xiaojinzi.componentdemo.test.TestContext;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 测试代码质量的界面
 */
@RouterAnno(
        path = ModuleConfig.App.TEST_QUALITY,
        desc = "测试代码质量的界面"
)
public class TestQualityAct extends BaseAct implements TestContext {

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

    @NonNull
    @Override
    public Context context() {
        return this;
    }

    @NonNull
    @Override
    public Fragment fragment() {
        return innerFragment;
    }

    @NonNull
    @Override
    public Dialog dialog() {
        return alertDialog;
    }

    @Override
    public void log(@NonNull String msg) {
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

    public void addTaskPassMsg(String taskName) {
        resultColor.setText(taskName + " 任务测试通过" + "\n" + resultColor.getText().toString());
    }

    public Completable wrapTask(Completable completable) {
        return completable
                .observeOn(Schedulers.io())
                .delay(1200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Completable allCancel() {
        return new CancelTest().execute(this);
    }

    private Completable allFailure() {
        return new FailureTest().execute(this);
    }

    private Completable allSuccess() {
        return new SuccessTest().execute(this);
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
                wrapTask(allSuccess()).doOnComplete(() -> addTaskPassMsg("allSuccess")),
                wrapTask(allCancel()).doOnComplete(() -> addTaskPassMsg("allCancel")),
                wrapTask(allFailure()).doOnComplete(() -> addTaskPassMsg("allFailure"))
        );

        observable
                .doOnSubscribe(disposable -> {
                    testFailure();
                    resultColor.setText("");
                })
                .subscribe(
                        () -> testSuccess(),
                        throwable -> {
                            testFailure(throwable);
                        }
                );

    }

    // -------------------------------------------------------- 失败的例子 -------------------------------start

    // -------------------------------------------------------- 失败的例子 -------------------------------end

    public Completable testWrap(TestBack testBack) {
        return Completable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            testBack.run(emitter);
        });
    }

    public Completable testWrapWithChildThread(TestBack testBack) {
        return Completable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            new Thread("child thread") {
                @Override
                public void run() {
                    testBack.run(emitter);
                }
            }.start();
        });
    }

    public void testCrash1(View view) {
        RxRouter
                .with(mContext)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_AUTORETURN1)
                .requestCodeRandom()
                .putString("data", "crashOnAfterJumpAction")
                .afterAction(() -> {
                    throw new NullPointerException("test exception on afterJumpAction");
                })
                .activityResultCall()
                .subscribe();
    }

    public void testCrash2(View view) {
        RxRouter
                .with(mContext)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.NOT_FOUND_TEST)
                .requestCodeRandom()
                .putString("data", "crashOnAfterErrorAction")
                .afterErrorAction(() -> {
                    throw new NullPointerException("test exception on afterJumpAction");
                })
                .activityResultCall()
                .subscribe();
    }

    public void testCrash3(View view) {
        RxRouter
                .with(mContext)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_AUTORETURN1)
                .requestCodeRandom()
                .putString("data", "crashOnAfterEventAction")
                .afterEventAction(() -> {
                    throw new NullPointerException("test exception on afterJumpAction");
                })
                .activityResultCall()
                .subscribe();
    }

    private synchronized void lockTest(@NonNull Runnable runnable) {
        runnable.run();
    }

}
