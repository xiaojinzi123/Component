package com.ehi.component.impl.service;

import android.app.Application;

import com.ehi.component.service.IComponentHostService;

/**
 * 空实现
 */
abstract class EHiMuduleServiceImpl implements IComponentHostService {
    @Override
    public void onCreate(Application app) {
    }
    @Override
    public void onDestory() {
    }
}
