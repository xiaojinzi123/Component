package com.xiaojinzi.component.application;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

@UiThread
public interface IModuleNotifyChanged {

    /**
     * 当加载的模块发生变化的时候会调用
     * 并且多个模块如果被加载
     *
     * @param app {@link Application}
     */
    @UiThread
    void onModuleChanged(@NonNull Application app);

}
