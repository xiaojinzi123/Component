package com.ehi.component1.service;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.ehi.base.service.impl.component1.Component1Service;
import com.ehi.component1.view.Component1Fragment;

public class Component1ServiceImpl implements Component1Service {

    public Component1ServiceImpl(Context app) {
        Toast.makeText(app, "创建了 Component1Service 服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Fragment getFragment() {
        return new Component1Fragment();
    }

}
