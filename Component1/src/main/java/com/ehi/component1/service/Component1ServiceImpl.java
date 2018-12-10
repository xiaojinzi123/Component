package com.ehi.component1.service;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.ehi.base.service.inter.component1.Component1Service;
import com.ehi.component.anno.EHiServiceAnno;
import com.ehi.component1.view.Component1Fragment;

@EHiServiceAnno(value = {Component1Service.class}, singleTon = false)
public class Component1ServiceImpl implements Component1Service {

    private Context context;

    public Component1ServiceImpl(@NonNull Application app) {
        context = app;
        Toast.makeText(app, "创建了 Component1Service 服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Fragment getFragment() {
        return new Component1Fragment();
    }

}
