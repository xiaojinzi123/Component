package com.xiaojinzi.component1.service;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

import com.xiaojinzi.base.service.inter.component1.Component1Service;
import com.xiaojinzi.component.anno.ServiceAnno;
import com.xiaojinzi.component1.view.Component1Fragment;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;

//@ServiceAnno(value = {Component1Service.class})
@ServiceAnno(Component1Service.class)
public class Component1ServiceImpl implements Component1Service {

    private Random r = new Random();
    private Context context;

    public Component1ServiceImpl(@NonNull Application app) {
        context = app;
        Toast.makeText(app, "创建了 Component1Service 服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Fragment getFragment() {
        return new Component1Fragment();
    }

    @Override
    public void doSomeThing() {
        Toast.makeText(context, "调用了 doSomeThing 方法", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Single<String> testError() throws Exception {
        int value = r.nextInt(1000);
        value = 1;
        if (value % 2 == 0) {
            return Single.error(new NullPointerException("test error"));
        }else {
            return Single.just("test error")
                    .delay(2, TimeUnit.SECONDS);
        }
    }

}
