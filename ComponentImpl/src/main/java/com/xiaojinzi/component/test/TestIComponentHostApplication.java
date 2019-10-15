package com.xiaojinzi.component.test;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaojinzi.component.application.IComponentHostApplication;

public class TestIComponentHostApplication implements IComponentHostApplication {
    @NonNull
    @Override
    public String getHost() {
        return null;
    }

    @Override
    public void onCreate(@NonNull Application app) {

    }

    @Override
    public void onDestroy() {

    }
}
