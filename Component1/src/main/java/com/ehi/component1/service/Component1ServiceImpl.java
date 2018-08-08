package com.ehi.component1.service;

import android.support.v4.app.Fragment;

import com.ehi.base.service.component1.Component1Service;
import com.ehi.component1.Component1Fragment;

public class Component1ServiceImpl implements Component1Service {

    @Override
    public Fragment getFragment() {
        return new Component1Fragment();
    }

}
