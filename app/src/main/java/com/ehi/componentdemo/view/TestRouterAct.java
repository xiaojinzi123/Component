package com.ehi.componentdemo.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ehi.base.InterceptorConfig;
import com.ehi.base.ModuleConfig;
import com.ehi.base.interceptor.DialogShowInterceptor;
import com.ehi.base.view.BaseAct;
import com.ehi.component.ComponentConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.bean.EHiActivityResult;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.impl.EHiRouterErrorResult;
import com.ehi.component.impl.EHiRouterInterceptor;
import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.impl.EHiRouterResult;
import com.ehi.component.impl.EHiRxRouter;
import com.ehi.component.support.EHiCallbackAdapter;
import com.ehi.component.support.NavigationDisposable;
import com.ehi.componentdemo.R;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TestRouterAct extends BaseAct {

    @EHiRouterAnno(
            host = ModuleConfig.App.NAME,
            value = ModuleConfig.App.TEST_ROUTER,
            desc = "测试跳转的界面"
    )
    public static void startActivity(@NonNull EHiRouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), TestRouterAct.class);
        request.getRawContext().startActivity(intent);
    }

    private TextView tv_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_router_act);
        getSupportActionBar().setTitle("高级路由测试");
        tv_detail = findViewById(R.id.tv_detail);
    }

    private void addInfo(@NonNull String info) {
        tv_detail.setText(tv_detail.getText() + "\n\n" + info);
    }

    private void addInfo(@Nullable EHiRouterResult routerResult, @Nullable Throwable error, @NonNull String url, @Nullable Integer requestCode) {
        if (requestCode == null) {
            if (routerResult != null) {
                tv_detail.setText(tv_detail.getText() + "\n\n普通跳转成功,目标:" + url);
            } else {
                tv_detail.setText(tv_detail.getText() + "\n\n普通跳转失败,目标:" + url + ",error = " + error.getClass().getSimpleName() + " ,errorMsg = " + error.getMessage());
            }
        } else {
            if (routerResult != null) {
                tv_detail.setText(tv_detail.getText() + "\n\nRequestCode=" + requestCode + "普通跳转成功,目标:" + url);
            } else {
                tv_detail.setText(tv_detail.getText() + "\n\nRequestCode=" + requestCode + "普通跳转失败,目标:" + url + ",error = " + error.getClass().getSimpleName() + " ,errorMsg = " + error.getMessage());
            }
        }
    }

    public void openUriTest(View view) {
        EHiRouter.with(this)
                .url("EHi://component1/testQuery?name=我是名称&pass=我是密码")
                .navigate();
    }

    public void goToInOtherModuleView(View view) {
        EHiRouter
                .with(TestRouterAct.this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_IN_OTHER_MODULE)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST_IN_OTHER_MODULE, null);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST_IN_OTHER_MODULE, null);
                    }
                });
    }

    public void normalJump(View view) {
        EHiRouter
                .with(TestRouterAct.this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .query("data", "normalJump")
                .putString("name", "cxj1")
                .putInt("age", 25)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST + "?data=normalJump", null);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST + "data=normalJump", null);
                    }
                });
    }

    public void normalJumpTwice(View view) {

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .query("data", "normalJumpTwice1")
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, "component1/test?data=normalJumpTwice1", null);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), "component1/test?data=normalJumpTwice1", null);
                    }
                });

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .query("data", "normalJumpTwice2")
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, "component1/test?data=normalJumpTwice2", null);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), "component1/test?data=normalJumpTwice2", null);
                    }
                });

    }

    public void jumpGetData(View view) {

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .query("data", "jumpGetData")
                .requestCode(123)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, "component1/test?data=jumpGetData", 123);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), "component1/test?data=jumpGetData", 123);
                    }
                });

    }

    public void rxJumpGetData(View view) {

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .query("data", "rxJumpGetData")
                .requestCode(456)
                .intentCall()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        tv_detail.setText(tv_detail.getText() + "\n\nrequestCode=456,目标:component1/test?data=rxJumpGetData,获取目标页面数据成功啦：Data = " + intent.getStringExtra("data"));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tv_detail.setText(tv_detail.getText() + "\n\nrequestCode=456,目标:component1/test?data=rxJumpGetData,获取目标页面数据失败,error = " + throwable.getClass().getSimpleName() + " ,errorMsg = " + throwable.getMessage());
                    }
                });

    }

    public void rxJumpGetDataStartWithTask(final View view) {

        SingleTransformer<String, Intent> transformer = new SingleTransformer<String, Intent>() {
            @Override
            public SingleSource<Intent> apply(Single<String> upstream) {
                return EHiRxRouter
                        .with(mContext)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST)
                        .query("data", "rxJumpGetDataFromQuery")
                        .putString("data", "rxJumpGetDataFromBundle")
                        .requestCode(789)
                        .intentCall();
            }
        };

        Single
                .just("value")
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        view.setEnabled(false);
                        tv_detail.setText(tv_detail.getText() + "\n\n耗时2秒,模拟耗时任务后执行跳转界面拿数据");
                    }
                })
                .observeOn(Schedulers.io())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEvent(new BiConsumer<String, Throwable>() {
                    @Override
                    public void accept(String s, Throwable throwable) throws Exception {
                        view.setEnabled(true);
                    }
                })
                .compose(transformer)
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        tv_detail.setText(tv_detail.getText() + "\n\nrequestCode=789,目标:component1/test?data=rxJumpGetData,执行耗时任务后获取目标页面数据成功啦：Data = " + intent.getStringExtra("data"));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tv_detail.setText(tv_detail.getText() + "\n\nrequestCode=789,目标:component1/test?data=rxJumpGetData,执行耗时任务后获取目标页面数据失败,error = " + throwable.getClass().getSimpleName() + " ,errorMsg = " + throwable.getMessage());
                    }
                });

    }

    public void jumpWithInterceptor(View view) {
        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .query("data", "jumpWithInterceptor")
                .requestCode(123)
                .interceptors(new EHiRouterInterceptor() {
                    @Override
                    public void intercept(final Chain chain) throws Exception {
                        final ProgressDialog dialog = ProgressDialog.show(chain.request().getRawContext(), "温馨提示", "耗时操作进行中,2秒后结束", true, false);
                        dialog.show();
                        Single
                                .fromCallable(new Callable<String>() {
                                    @Override
                                    public String call() throws Exception {
                                        return "test";
                                    }
                                })
                                .delay(2, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        dialog.dismiss();
                                        chain.proceed(chain.request());
                                    }
                                });
                    }
                })
                .interceptorNames(InterceptorConfig.USER_LOGIN)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, "component1/test?data=jumpWithInterceptor", 123);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), "component1/test?data=jumpWithInterceptor", 123);
                    }
                });
    }

    public void clearInfo(View view) {
        tv_detail.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            tv_detail.setText(tv_detail.getText() + "\n\nrequestCode=" + requestCode + "的跳转返回数据啦：Data = " + data.getStringExtra("data"));
        } /*else {
            tv_detail.setText(tv_detail.getText() + "\n requestCode=" + requestCode + "的跳转返回数据啦,但是未被处理");
        }*/
    }

    public void testQueryPass(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_QUERY)
                .query("name", "我是小金子")
                .query("pass", "我是小金子的密码")
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST_QUERY + "?name=我是小金子&pass=我是小金子的密码", null);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST_QUERY, null);
                    }
                });
    }

    public void testLogin(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_LOGIN)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST_LOGIN, null);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST_LOGIN, null);
                    }
                });
    }

    public void testDialog(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_DIALOG)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST_DIALOG, null);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST_DIALOG, null);
                    }
                });
    }

    public void testGotoKotlin(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.Module2.NAME)
                .path(ModuleConfig.Module2.MAIN)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, ModuleConfig.Module2.NAME + "/" + ModuleConfig.Module2.MAIN, null);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), ModuleConfig.Module2.NAME + "/" + ModuleConfig.Module2.MAIN, null);
                    }
                });
    }

    public void testCustomIntent(View view) {

        EHiRouter
                .with(this)
                .host(ModuleConfig.Module2.NAME)
                .path(ModuleConfig.Module2.MAIN)
                .onIntentCreated(new com.ehi.component.support.Consumer<Intent>() {
                    @Override
                    public void accept(@NonNull Intent intent) throws Exception {
                        Toast.makeText(TestRouterAct.this, intent.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        addInfo(result, null, ModuleConfig.Module2.NAME + "/" + ModuleConfig.Module2.MAIN, null);
                    }

                    @Override
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), ModuleConfig.Module2.NAME + "/" + ModuleConfig.Module2.MAIN, null);
                    }
                });

    }

    public void testMatchesResultCode(View v) {

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .requestCode(123)
                .query("data", "testMatchesResultCode")
                .resultCodeMatchCall(RESULT_OK)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        addInfo("从" + ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST + "界面返回了,并且成功匹配 resultCode = Activity.RESULT_OK");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        addInfo("打算匹配 " + ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST + "界面返回的 resultCode = Activity.RESULT_OK 失败,错误信息：" + throwable.getMessage());
                    }
                });

    }

    public void testUseRequestCodeTiwce(View v) {

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .requestCode(123)
                .query("data", "testUseRequestCodeTiwce1")
                .resultCodeMatchCall(RESULT_OK)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        addInfo("从" + ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST + "界面返回了,并且成功匹配 resultCode = Activity.RESULT_OK");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        addInfo("错误信息：" + throwable.getMessage());
                    }
                });

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_DIALOG)
                .requestCode(123)
                .query("data", "testUseRequestCodeTiwce2")
                .resultCodeMatchCall(RESULT_OK)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        addInfo("从" + ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST + "界面返回了,并且成功匹配 resultCode = Activity.RESULT_OK");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        addInfo("错误信息：" + throwable.getMessage());
                    }
                });

    }

    public void testCustomerCallPhone(View view) {

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.CALL_PHONE)
                .requestCode(123)
                .activityResultCall()
                .subscribe(new Consumer<EHiActivityResult>() {
                    @Override
                    public void accept(EHiActivityResult activityResult) throws Exception {
                        addInfo("您从打电话界面回来啦,resultCode = " + activityResult.resultCode + ",resultCode = " + activityResult.resultCode);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        addInfo("跳转到打电话界面失败啦：" + throwable.getMessage());
                    }
                });

    }

    public void testCustomerJump(View view) {

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.SYSTEM_APP_DETAIL)
                .requestCode(123)
                .activityResultCall()
                .subscribe(new Consumer<EHiActivityResult>() {
                    @Override
                    public void accept(EHiActivityResult activityResult) throws Exception {
                        addInfo("您从App详情回来啦,resultCode = " + activityResult.resultCode + ",resultCode = " + activityResult.resultCode);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        addInfo("跳转到App详情失败啦：" + throwable.getMessage());
                    }
                });

    }

    public void testCallbackAfterFinish(View view) {

        NavigationDisposable navigationDisposable = EHiRxRouter
                .with(this)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.CALL_PHONE)
                .interceptors(DialogShowInterceptor.class)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onEvent(@Nullable EHiRouterResult result, @Nullable EHiRouterErrorResult errorResult) {
                        // 这里的代码不会被调用
                    }

                    @Override
                    public void onCancel(@NonNull EHiRouterRequest request) {
                        super.onCancel(request);
                        Toast.makeText(ComponentConfig.getApplication(), "被自动取消了", Toast.LENGTH_SHORT).show();
                    }
                });

        //navigationDisposable.cancel();

        finish();
    }

    public void testBeforAndAfterAction(View view) {

        EHiRxRouter
                .with(this)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.CALL_PHONE)
                .onBeforJump(new com.ehi.component.support.Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(mContext, "startActivity之前", Toast.LENGTH_SHORT).show();
                    }
                })
                .onAfterJump(new com.ehi.component.support.Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(mContext, "startActivity之后", Toast.LENGTH_SHORT).show();
                    }
                })
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onEvent(@Nullable EHiRouterResult result, @Nullable EHiRouterErrorResult errorResult) {
                    }
                });

    }

    public void testFragmentJump(View view) {
        EHiRouter.with(mContext)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_FRAGMENT_ROUTER)
                .navigate();
    }

}
