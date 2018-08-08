package com.ehi.api.impl;

import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.api.EHIComponentUtil;
import com.ehi.api.application.IComponentApplication;
import com.ehi.api.application.IComponentHostApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类是生成的 Module Application 类的父类
 * 如果名称更改了,请配置到 {@link EHIComponentUtil#IMPL_OUTPUT_PKG} 和 {@link EHIComponentUtil#MODULE_APPLICATION_IMPL_CLASS_NAME} 上
 */
public abstract class EHiModuleApplicationImpl implements IComponentHostApplication {

    /**
     * Application的集合
     */
    protected List<IComponentApplication> moduleAppList = new ArrayList<>();

    /**
     * 是否初始化了list,懒加载
     */
    protected boolean hasInitList = false;

    protected void initList() {
        hasInitList = true;
    }

    @Override
    public void onCreate(@NonNull Application app) {

        if (!hasInitList) {
            initList();
        }

        if (moduleAppList == null) {
            return;
        }

        for (IComponentApplication application : moduleAppList) {
            application.onCreate(app);
        }

    }

    @Override
    public void onDestory() {

        if (!hasInitList) {
            initList();
        }

        if (moduleAppList == null) {
            return;
        }

        for (IComponentApplication application : moduleAppList) {
            application.onDestory();
        }

    }

}
