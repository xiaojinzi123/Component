package com.xiaojinzi.component.impl.service;

import android.app.Application;

import com.xiaojinzi.component.service.IComponentHostService;

/**
 * 空实现,每一个模块的 service 生成类需要继承的
 *
 * @author xiaojinzi
 */
abstract class MuduleServiceImpl implements IComponentHostService {
    @Override
    public void onCreate(Application app) {
    }

    @Override
    public void onDestroy() {
    }
}
