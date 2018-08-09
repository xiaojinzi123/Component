package com.ehi.componentdemo;

import android.app.Application;

import com.ehi.component.impl.EHiModuleManager;

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EHiModuleManager.init(this);

    }
}
