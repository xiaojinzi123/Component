package com.ehi.componentdemo;

import android.app.Application;

import com.ehi.api.impl.EHiModuleManager;

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EHiModuleManager.init(this);

    }
}
