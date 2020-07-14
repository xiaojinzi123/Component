package com.xiaojinzi.component.impl.service;

import android.app.Application;

import com.xiaojinzi.component.service.IComponentHostService;

/**
 * 空实现,每一个模块的 service 生成类需要继承的
 *
 * @author xiaojinzi
 */
abstract class ModuleServiceImpl implements IComponentHostService {
    @Override
    public void onModuleCreate(Application app) {
    }

    @Override
    public void onModuleDestroy() {
    }
}
