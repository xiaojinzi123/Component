package com.xiaojinzi.component.support;

import android.app.Application;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.xiaojinzi.component.ComponentConstants;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

/**
 * 模块的生命周期接口
 */
@MainThread
@CheckClassNameAnno
public interface IModuleLifecycle {

    @MainThread
    void onCreate(@NonNull Application app);

    @MainThread
    void onDestroy();

}
