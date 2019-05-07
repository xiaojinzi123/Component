package com.xiaojinzi.componentdemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.BiCallback;
import com.xiaojinzi.component.impl.Callback;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.impl.RxRouter;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.componentdemo.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

@RouterAnno(path = ModuleConfig.App.NAME, desc = "主界面")
public class MainAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        getSupportActionBar().setTitle("组件化方案:(路由、服务、生命周期)");
    }

    public void loadAppModule(View v) {
        ModuleManager.getInstance().register(ModuleConfig.App.NAME);
    }

    public void unloadAppModule(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.App.NAME);
    }

    public void loadUserModule(View v) {
        ModuleManager.getInstance().register(ModuleConfig.User.NAME);
    }

    public void unloadUserModule(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.User.NAME);
    }

    public void loadHelpModule(View v) {
        ModuleManager.getInstance().register(ModuleConfig.Help.NAME);
    }

    public void unloadHelpModule(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.Help.NAME);
    }

    public void loadModule1Module(View v) {
        ModuleManager.getInstance().register(ModuleConfig.Module1.NAME);
    }

    public void unloadModule1Module(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.Module1.NAME);
    }

    public void loadModule2Module(View v) {
        ModuleManager.getInstance().register(ModuleConfig.Module2.NAME);
    }

    public void unloadModule2Module(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.Module2.NAME);
    }

    public void testRouter(View view) {

        /*RxRouter
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_ROUTER)
                .call()
                .subscribe();*/

        /*Router
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .putString("data", "rxJumpGetData")
                .requestCode(456)
                .navigateForIntent(new BiCallback<Intent>() {
                    @Override
                    public void onSuccess(@NonNull RouterResult result, @NonNull Intent intent) {
                        System.out.println("123123");
                    }

                    @Override
                    public void onError(@NonNull RouterErrorResult errorResult) {
                        System.out.println("123123");
                    }

                    @Override
                    public void onCancel(@NonNull RouterRequest originalRequest) {
                        System.out.println("123123");
                    }
                });*/

        Router
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .putString("data", "rxJumpGetData")
                .requestCode(456)
                .newCall()
                .execute(new CallbackAdapter() {
                    @Override
                    public void onEvent(@Nullable RouterResult successResult, @Nullable RouterErrorResult errorResult) {
                        super.onEvent(successResult, errorResult);
                    }
                });

    }

    public void testWebRouter(View view) {
        Router
                .with(this)
                .host(ModuleConfig.Help.NAME)
                .path(ModuleConfig.Help.TEST_WEB_ROUTER)
                .navigate();
    }

    public void testQuality(View view) {
        Router
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_QUALITY)
                .navigate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Toast.makeText(this, "返回数据啦", Toast.LENGTH_SHORT).show();
        }
    }

    public void testService(View view) {

        List<String> list = new ArrayList<>();
        list.add("123123");

        Observable<String> observable = Single.just(list)
                .flatMapObservable(new Function<List<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(List<String> strings) throws Exception {
                        return Observable.fromIterable(strings);
                    }
                });

        Router
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_SERVICE)
                .navigate();
    }

}
