package com.xiaojinzi.component.support;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

/**
 * 基础的生命周期接口
 */
@UiThread
@CheckClassNameAnno
public interface IBaseLifecycle {

    @UiThread
    void onCreate(@NonNull Application app);

    @UiThread
    void onDestroy();

}
