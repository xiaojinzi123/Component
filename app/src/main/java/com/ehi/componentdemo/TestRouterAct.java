package com.ehi.componentdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouterResult;
import com.ehi.component.impl.EHiRxRouter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@EHiRouterAnno(value = "testRouter", desc = "测试跳转的界面")
public class TestRouterAct extends AppCompatActivity {

    private TextView tv_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_router_act);

        tv_detail = findViewById(R.id.tv_detail);

    }

    public void normalJump(View view) {

        EHiRouterResult routerResult = EHiRxRouter
                .with(this)
                .host("component1")
                .path("test")
                .query("data", "normalJump")
                .navigate();

        addInfo(routerResult, "component1/test?data=normalJump", null);

    }

    public void normalJumpTwice(View view) {

        EHiRouterResult routerResult1 = EHiRxRouter
                .with(this)
                .host("component1")
                .path("test")
                .query("data", "normalJumpTwice")
                .navigate();

        EHiRouterResult routerResult2 = EHiRxRouter
                .with(this)
                .host("component1")
                .path("test")
                .query("data", "normalJumpTwice")
                .navigate();

        addInfo(routerResult1, "component1/test?data=normalJumpTwice", 123);
        addInfo(routerResult2, "component1/test?data=normalJumpTwice", 123);


    }

    public void jumpGetData(View view) {

        EHiRouterResult routerResult = EHiRxRouter
                .with(this)
                .host("component1")
                .path("test")
                .query("data", "jumpGetData")
                .requestCode(123)
                .navigate();

        addInfo(routerResult, "component1/test?data=jumpGetData", 123);

    }

    public void rxJumpGetData(View view) {

        EHiRxRouter
                .with(this)
                .host("component1")
                .path("test")
                .query("data", "rxJumpGetData")
                .requestCode(456)
                .newIntentCall()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        tv_detail.setText(tv_detail.getText() + "\n\nrequestCode=456,目标:component1/test?data=rxJumpGetData,获取目标页面数据成功啦：Data = " + intent.getStringExtra("data"));
                    }
                });

    }

    public void rxJumpGetDataStartWithTask(final View view) {

        SingleTransformer<String, Intent> transformer = EHiRxRouter
                .with(this)
                .host("component1")
                .path("test")
                .query("data", "rxJumpGetDataFromQuery")
                .putString("data", "rxJumpGetDataFromBundle")
                .requestCode(789)
                .newIntentTransformer();

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

    private void addInfo(EHiRouterResult routerResult, @NonNull String url, @Nullable Integer requestCode) {
        if (requestCode == null) {
            if (routerResult.isSuccess()) {
                tv_detail.setText(tv_detail.getText() + "\n\n普通跳转成功,目标:" + url);
            } else {
                tv_detail.setText(tv_detail.getText() + "\n\n普通跳转失败,目标:" + url + ",error = " + routerResult.getError().getClass().getSimpleName() + " ,errorMsg = " + routerResult.getError().getMessage());
            }
        } else {
            if (routerResult.isSuccess()) {
                tv_detail.setText(tv_detail.getText() + "\n\nRequestCode=" + requestCode + "普通跳转成功,目标:" + url);
            } else {
                tv_detail.setText(tv_detail.getText() + "\n\nRequestCode=" + requestCode + "普通跳转失败,目标:" + url + ",error = " + routerResult.getError().getClass().getSimpleName() + " ,errorMsg = " + routerResult.getError().getMessage());
            }
        }

    }

}
