package com.ehi.component2.service;

import java.lang.System;

/**
 * * time   : 2018/12/06
 * *
 * * @author : xiaojinzi 30212
 */
@com.ehi.component.anno.EHiServiceAnno(singleTon = false, value = {com.ehi.base.service.inter.component2.Component2Service.class})
@kotlin.Metadata(mv = {1, 1, 13}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\b\u001a\u00020\tH\u0016R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\u0004\u00a8\u0006\n"}, d2 = {"Lcom/ehi/component2/service/Component2ServiceImpl;", "Lcom/ehi/base/service/inter/component2/Component2Service;", "app", "Landroid/app/Application;", "(Landroid/app/Application;)V", "getApp", "()Landroid/app/Application;", "setApp", "doSomeThing", "", "Component2_release"})
public final class Component2ServiceImpl implements com.ehi.base.service.inter.component2.Component2Service {
    @org.jetbrains.annotations.NotNull()
    private android.app.Application app;
    
    @org.jetbrains.annotations.NotNull()
    public final android.app.Application getApp() {
        return null;
    }
    
    public final void setApp(@org.jetbrains.annotations.NotNull()
    android.app.Application p0) {
    }
    
    @java.lang.Override()
    public void doSomeThing() {
    }
    
    public Component2ServiceImpl(@org.jetbrains.annotations.NotNull()
    android.app.Application app) {
        super();
    }
}