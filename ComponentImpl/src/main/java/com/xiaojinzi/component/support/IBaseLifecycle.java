package com.xiaojinzi.component.support;

import android.app.Application;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

/**
 * 基础的生命周期接口
 */
@MainThread
@CheckClassNameAnno
public interface IBaseLifecycle {

    @MainThread
    void onCreate(@NonNull Application app);

    @MainThread
    void onDestroy();

}
