package com.ehi.component1.service;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.ehi.base.service.inter.component1.Component1Service;
import com.ehi.base.service.inter.component2.Component2Service;
import com.ehi.component.anno.EHiServiceAnno;
import com.ehi.component1.view.Component1Fragment;

@EHiServiceAnno(value = {Component1Service.class, Component2Service.class})
public class Component1ServiceImpl implements Component1Service, Component2Service {

    private Context context;

    public Component1ServiceImpl(Context app) {
        context = app;
        Toast.makeText(app, "创建了 Component1Service 服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Fragment getFragment() {
        return new Component1Fragment();
    }

    @Override
    public void doSomeThing() {
        Toast.makeText(context, "doSomeThing", Toast.LENGTH_SHORT).show();
    }

}
